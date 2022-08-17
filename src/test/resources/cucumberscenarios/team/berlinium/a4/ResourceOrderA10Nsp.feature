@DIGIHUB-126401 @DIGIHUB-106469
Feature: Receive and process Resource Orders for A10NSP
  See https://gard.telekom.de/gardwiki/display/DGHB/Fulfillment+-+A10NSP+interface+-+Resource+Order+Management for details.

  Note: These scenarios and steps are a first draft! Maybe this changes, to make them more understandable
  for stakeholders...

  #@DIGIHUB-150008
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
    And a NSP A10NSP "A" with vlanRangeLower "10" and vlanRangeUpper "20" connected to TP "A"
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
      | NEL Reference | Action Type |
      | A             | ADD         |
      | B             | ADD         |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

  #@DIGIHUB-xxxxxx
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with Action Type Delete - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Reference | Action Type |
      | A             | DELETE      |
      | B             | DELETE      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

  #@DIGIHUB-xxxxxx
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with Action Type Modify - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Reference | Action Type |
      | A             | MODIFY      |
      | B             | MODIFY      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

  #@DIGIHUB-xxxxxx
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with Action Type NoChange - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Reference | Action Type |
      | A             | NOCHANGE    |
      | B             | NOCHANGE    |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

  #@DIGIHUB-xxxxxx
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO, 2 items with different Action Types
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Reference | Action Type |
      | A             | ADD         |
      | B             | DELETE      |
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

  #@DIGIHUB-xxxxxx
  Scenario Outline: Sputnik sends resource order MODIFY with vlan range Vlan Range ROI >= (Vlan Range NSP A10NSP + NSPs L2BSA in state PLANNING)
    Given <NumberTpsAndNsps> TPs with identical carrierBsaReference and NSPs L2BSA with lifecycleState "<lcState>" connected to NEG "A"
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Reference | Action Type | VLAN Range Lower | VLAN Range Upper |
      | A             | MODIFY      | <VlanLower>      | <VlanUpper>      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"

    Examples:
      | NumberTpsAndNsps | lcState  | VlanLower | VlanUpper |
#      | 3               | PLANNING | 10        | 100       |
      | 0               | PLANNING | 10        | 20        |
#      | 30              | PLANNING | 10        | 10        |

  #@DIGIHUB-xxxxxx
  Scenario Outline: Sputnik sends resource order MODIFY with Vlan Range ROI < (Vlan Range NSP A10NSP + NSPs L2BSA in state PLANNING)
    Given <NumberTpsAndNsps> TPs with identical carrierBsaReference and NSPs L2BSA with lifecycleState "<lcState>" connected to NEG "A"
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Reference | Action Type | VLAN Range Lower | VLAN Range Upper |
      | A             | MODIFY      | <VlanLower>      | <VlanUpper>      |
    Then the request is responded with HTTP error code 201
    And the resource order is saved in RO database
    And the resource order state is "rejected"
    And all order item states are "rejected"
    Examples:
      | NumberTpsAndNsps | lcState  | VlanLower | VlanUpper |
      | 3                | PLANNING | 10        | 10        |
#      | 20              | INSTALLING | 10        | 20        |
