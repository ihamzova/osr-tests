Feature: [DIGIHUB-35444][DIGIHUB-39620][OS&R] Receive and process Termination Points and extend AccessLine inventory
  # https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=326048438

  # X-Ray: DIGIHUB-59383
  @domain
  @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-carrier-management
  Scenario: NEMO creates Termination Point with preprovisioning triggered
    Given a NEP is existing in A4 resource inventory
    When NEMO sends a create TP request with type "PON_TP"
    Then the request is responded with HTTP code 201
    # --- Add any U-Piter Then-steps here ---
    And the TP does exist in A4 resource inventory
    And a NSP FTTH connected to the TP does exist in A4 resource inventory
    And 1 "PUT" NSP FTTH update notification was sent to NEMO
