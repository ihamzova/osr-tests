# Add epic DIGIHUB number here
Feature: Receive and process Resource Orders for A10NSP
  See https://gard.telekom.de/gardwiki/display/DGHB/Fulfillment+-+A10NSP+interface+-+Resource+Order+Management for details.

  #@DIGIHUB-150008
  Background:
  Work in progress!
    Given a NEG with type "POD" is existing in A4 resource inventory
#    And a NE "A" with type "" and category "" is existing in A4 resource inventory
#    And another NE "B" with type "" and category "" is existing in A4 resource inventory
#    And a NEP "A" with type "100G_ETHERNET_PORT" and functional label "100G_001" connected to NE "A" is existing in A4 resource inventory
#    And another NEP "B" with type "" and functional label "" connected to NE "B" is existing in A4 resource inventory
#    And a NEL "A" connected to NEPs "A" and "B" is existing in A4 resource inventory
#    And the REBELL wiremock will respond HTTP code 200 when called
#    And the REBELL wiremock response will include a vendorPortNameA "A" and vendorPortName "B" with reference to NE "A", NE "B" and NEL "A"
#    And the Merlin wiremock will respond HTTP code 200 when called

  # Add DIGIHUB xray ticket here
#  Scenario: Receive RO, 1 item - Sunny Day
#    Given a prepared resource order "A" with reference to NEL "A"
#    And the resource order "A" has an order item "A" with action type "ADD"
#    When CAD@Sputnik sends the prepared resource order with empty resource order ID
#    Then the request is responded with HTTP error code 201
#    And the response contains a uuid
#    And the resource order "A" is saved in RO database
#    And the resource order "A" has state "COMPLETED"
#    And ...

    # Add DIGIHUB xray ticket here
#  Scenario: Receive RO, 2 items - Sunny Day
#    Given another NE "C" with type "" and category ""
#    And another NEP "C" connected to NE "C"
#    And another NEL "B" connected to NEPs "A" and "B"
#  And a prepared resource order "A" with reference to NEL "A"
#    And the resource order "A" has an order item "A" with action type "ADD"
#    And the resource order "A" has another order item "B" with action type "DELETE"
#    And the REBELL wiremock response will include another vendorPortNameA "C" and vendorPortName "D" with reference to NE "A", NE "C" and NEL "B"
#    When CAD@Sputnik sends the prepared resource order with empty resource order ID
#    Then the request is responded with HTTP error code 201
#    And the response contains a uuid
#    And the resource order "A" is saved in RO database
#    And the resource order "A" has state "COMPLETED"
#    And ...

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
