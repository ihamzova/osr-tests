@DIGIHUB-126401 @DIGIHUB-106469
Feature: Receive and process Resource Orders for A10NSP
  See https://gard.telekom.de/gardwiki/display/DGHB/Fulfillment+-+A10NSP+interface+-+Resource+Order+Management for details.

  Note: These scenarios and steps are a first draft! Maybe this changes, to make them more understandable
  for stakeholders...

  #@DIGIHUB-150008 #@DIGIHUB-163474 #@DIGIHUB-163475
  Background:
    Given a NEG with type "POD"
    And a NE "A" with type "A4-A10NSP-Switch-v1" and category "A10NSP_SWITCH"
    And a NE "B" with type "A4-OLT-v1" and category "OLT"
    And a NE "C" with type "A4-A10NSP-Switch-v1" and category "A10NSP_SWITCH"
    And a NEP "A" with type "100G_ETHERNET_PORT" and functional label "100G_001" connected to NE "A"
    And a NEP "B" with type "10G_ETHERNET_PORT" and functional label "10G_002" connected to NE "B"
    And a NEP "C" with type "10G_ETHERNET_PORT" and functional label "10G_001" connected to NE "C"
    And a NEL "A" connected to NEPs "A" and "B"
    And a NEL "B" connected to NEPs "A" and "C"
    And a TP "A" with type "A10NSP_TP" connected to NEP "A"
    And a TP "B" with type "A10NSP_TP" connected to NEP "B"
    And a TP "C" with type "A10NSP_TP" connected to NEP "C"
    And a NSP A10NSP "A" with vlanRangeLower "10" and vlanRangeUpper "20" and lifeCycleState "PLANNING" connected to TP "A"
    And a NSP A10NSP "B" connected to TP "B"
    And a NSP A10NSP "C" connected to TP "C"
    And the REBELL wiremock will respond HTTP code 200 when called for NE "A", with the following data:
      | NEL Reference | Vendor Port Name A | Vendor Port Name B |
      | A             | 100G_001           | 10ge 0/2           |
      | B             | 100G_001           | 0/0/1              |

  @DIGIHUB-151803
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with Action Type Add - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Ref (LBZ) | NSP A10NSP Ref (carrierBsaRef) | Action Type |
      | A             | A                              | ADD         |
      | B             | A                              | ADD         |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

  @DIGIHUB-163468
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with Action Type Delete - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Ref (LBZ) | NSP A10NSP Ref (carrierBsaRef) | Action Type |
      | A             | A                              | DELETE      |
      | B             | A                              | DELETE      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

  @DIGIHUB-163469
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with Action Type Modify - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Ref (LBZ) | NSP A10NSP Ref (carrierBsaRef) | Action Type |
      | A             | A                              | MODIFY      |
      | B             | A                              | MODIFY      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"


  #DIGIHUB-77248 - build A10NSP "modify" use case for Resource Order Item
  @DIGIHUB-163467
  @team:berlinium
  @ms:a4-resource-order-orchestrator @ms:a4-resource-inventory @ms:a4-inventory-importer @ms:a4-carrier-management @ms:a4-nemo-updater
  Scenario: Receive RO, 1 item with Action Type Modify - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Ref (LBZ) | NSP A10NSP Ref (carrierBsaRef) |Action Type |
      | A             | A                              |MODIFY      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"
    And 1 "GET" request was sent to the REBELL wiremock for NE "A"
    And all attributes from ResourceOrder for A10NSP "A" are saved in A4 resource inventory
    And the A10NSP "A" lifecycleState is still "PLANNING" in the A4 resource inventory
    And 1 "PUT" NSP A10NSP "A" update notification was sent to NEMO

  @DIGIHUB-163470
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with Action Type NoChange - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Ref (LBZ) | NSP A10NSP Ref (carrierBsaRef) | Action Type |
      | A             | A                              | NOCHANGE    |
      | B             | A                              | NOCHANGE    |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

  @DIGIHUB-163471
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with different Action Types
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Ref (LBZ) | NSP A10NSP Ref (carrierBsaRef) | Action Type |
      | A             | A                              | ADD         |
      | B             | A                              | DELETE      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "rejected"
    And all order item states are "rejected"

  @DIGIHUB-150006
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO - Rainy Day, resource order ID is sent
    When CAD@Sputnik sends a resource order with filled resource order ID
    Then the request is responded with HTTP error code 400
    And the resource order is not saved in RO database
    And the RO is not added to A4 resource order queue

  @DIGIHUB-163472
  Scenario Outline: Sputnik sends resource order MODIFY with (Vlan Range ROI - Vlan Range NSP A10NSP) + number of NSPs L2BSA in state PLANNING >= 0
    Given <NumberTpsAndNsps> TPs with carrierBsaReference of NSP A10NSP "A" and NSPs L2BSA with lifecycleState "<lcState>" connected to the NEG
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Ref (LBZ) | NSP A10NSP Ref (carrierBsaRef) | Action Type | VLAN Range Lower | VLAN Range Upper |
      | A             | A                              | MODIFY      | <VlanLower>      | <VlanUpper>      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

    Examples:
      | NumberTpsAndNsps | lcState  | VlanLower | VlanUpper |
      | 3                | PLANNING | 10        | 100       |
      | 0                | PLANNING | 10        | 20        |
      | 30               | PLANNING | 10        | 10        |

  @DIGIHUB-163473
  Scenario Outline: Sputnik sends resource order MODIFY with (Vlan Range ROI - Vlan Range NSP A10NSP) + number of NSPs L2BSA in state PLANNING < 0
    Given <NumberTpsAndNsps> TPs with carrierBsaReference of NSP A10NSP "A" and NSPs L2BSA with lifecycleState "<lcState>" connected to the NEG
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Ref (LBZ) | NSP A10NSP Ref (carrierBsaRef) | Action Type | VLAN Range Lower | VLAN Range Upper |
      | A             | A                              | MODIFY      | <VlanLower>      | <VlanUpper>      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "rejected"
    And all order item states are "rejected"

    Examples:
      | NumberTpsAndNsps | lcState    | VlanLower | VlanUpper |
      | 3                | PLANNING   | 10        | 10        |
      | 30               | INSTALLING | 10        | 10        |
