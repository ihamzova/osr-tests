# Add epic DIGIHUB number here
Feature: Receive and process Resource Orders for A10NSP
  See https://gard.telekom.de/gardwiki/display/DGHB/Fulfillment+-+A10NSP+interface+-+Resource+Order+Management for details.

  #@DIGIHUB-xxxx
  Background:
    Work in progress!
    Given a NEG with type "POD" is existing in A4 resource inventory
    And a NE (called "NE#1") with type "" and category "" is existing in A4 resource inventory
    And another NE (called "NE#2") with type "" and category "" is existing in A4 resource inventory
    And a NEP (called "NEP#1") with type "100G_ETHERNET_PORT" and functional label "100G_001" connected to NE "NE#1" is existing in A4 resource inventory
    And another NEP (called "NEP#2") with type "" and functional label "" connected to NE "NE#2" is existing in A4 resource inventory
    And a NEL connected to NEPs "NEP#1" and "NEP#2" is existing in A4 resource inventory

  # Add DIGIHUB xray ticket here
#  Scenario: Receive RO - Sunny Day
#    When CAD@Sputnik sends a resource order with empty resource order ID
#    Then the request is responded with HTTP error code 201
#    And the response contains a resource order ID
#    And the resource order is saved in RO database
#    And the resource order state is "COMPLETED"
#    And ...

  # Add DIGIHUB xray ticket here
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
