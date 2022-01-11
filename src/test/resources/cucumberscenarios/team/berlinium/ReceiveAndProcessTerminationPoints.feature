Feature: [DIGIHUB-35444][Berlinium] Receive and process Termination Points
  # https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=326048438

  Background:
    Given a NEP is existing in A4 resource inventory

  @berlinium
  @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-carrier-management
  Scenario: NEMO creates Termination Point with preprovisioning triggered
    Given the wg-a4-provisioning preprovisioning mock will respond HTTP code 201 when called, and create the NSP
    When NEMO sends a create TP request with type "PON_TP"
    Then the request is responded with HTTP code 201
    And the TP does exist in A4 resource inventory
    And a DPU preprovisioning request to wg-a4-provisioning mock was triggered
    And a NSP FTTH connected to the TP does exist in A4 resource inventory

  @berlinium
  @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-carrier-management
  Scenario: Triggered preprovisioning - U-Piter not reachable; retry
    Given the wg-a4-provisioning preprovisioning mock will respond HTTP code 500 when called the 1st time
    And the wg-a4-provisioning preprovisioning mock will respond HTTP code 201 when called the 2nd time, and create the NSP
    When NEMO sends a create TP request with type "PON_TP"
    Then the request is responded with HTTP code 201
    And the TP does exist in A4 resource inventory
    And a DPU preprovisioning request to wg-a4-provisioning mock was triggered
    And the DPU preprovisioning request to wg-a4-provisioning mock is repeated after 3 minutes
    And a NSP FTTH connected to the TP does exist in A4 resource inventory
