@REG_DIGIHUB-128553
Feature: [DIGIHUB-128553][Berlinium] Nemo Status Update Test
  # here links to Gard

  # X-Ray: DIGIHUB-94384 NEMO sends a status patch for A4 Network Service Profile (L2BSA) with garbage value for Operational_State field, should be allowed"
  @berlinium @domain
  @a4-resource-inventory @a4-resource-inventory-service
  Scenario: NEMO sends a status patch for A4 Network Service Profile (L2BSA) with garbage value for Operational_State field, should be allowed
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And a NSP L2BSA with operationalState "NOT_WORKING" is existing in A4 resource inventory
  #  // When: Nemo wants to change operationalState to "Installing"
    When NEMO sends a request to change NSP L2BSA operationalState to "BlaBla"
    Then the request is responded with HTTP code 201
    And the NSP L2BSA operationalState is "NOT_WORKING"

  @berlinium @domain
  @a4-resource-inventory @a4-resource-inventory-service
  Scenario: NEMO sends a status patch for A4 Network Service Profile (L2BSA) with garbage value for Operational_State field, should be allowed
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And a NSP L2BSA with operationalState "NOT_WORKING" is existing in A4 resource inventory
  #  // When: Nemo wants to change operationalState to "Installing"
    When NEMO sends a request to change NSP L2BSA operationalState to "WORKING"
    Then the request is responded with HTTP code 201
    And the NSP L2BSA operationalState is "WORKING"