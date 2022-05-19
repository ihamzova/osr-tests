@DIGIHUB-126401
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
    And a NSP A10NSP "A" connected to TP "A"
    And a NSP A10NSP "B" connected to TP "B"
    And a NSP A10NSP "C" connected to TP "C"
    And the REBELL wiremock will respond HTTP code 200 when called for NE "A", with the following data:
      | NEL Reference | Vendor Port Name A | Vendor Port Name B |
      | A             | 100G_001           | 10ge 0/2           |
      | B             | 100G_001           | 0/0/1              |

  #@DIGIHUB-xxxxxx
  Scenario: Receive RO, 2 items - Sunny Day
    When CAD@Sputnik sends a resource order with the following order items:
      | NEL Reference | Action Type |
      | A             | ADD         |
      | B             | ADD         |
    Then the request is responded with HTTP error code 201
    And the response contains a resource order ID
    And the resource order is saved in RO database
    And the resource order state is "completed"
    And all order item states are "completed"
    And 1 "GET" request was sent to the REBELL wiremock for NE "A"
    And 1 "POST" request was sent to the A10NSP Inventory mock for the 1st order item

  @DIGIHUB-150006
  @team:berlinium
  @ms:a4-resource-order-orchestrator
  Scenario: Receive RO - Rainy Day, resource order ID is sent
    When CAD@Sputnik sends a resource order with filled resource order ID
    Then the request is responded with HTTP error code 400
    And the resource order is not saved in RO database
    And the RO is not added to A4 resource order queue

  # Add DIGIHUB xray ticket here
#  Scenario: Receive RO - Rainy Day, structure invalid
#    When CAD@Sputnik sends a resource order with invalid format
#    Then the request is responded with HTTP error code 400
#    And the resource order is not saved in RO database
