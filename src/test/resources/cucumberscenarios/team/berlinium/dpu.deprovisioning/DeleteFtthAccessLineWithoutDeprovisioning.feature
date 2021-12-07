@REG_DIGIHUB-118272
Feature: Berlinium parts of DPU Commissioning in A4 platform - Delete FTTH Access line without deprovisioning

  # X-Ray: DIGIHUB-127641
  @berlinium @domain @a4-resource-inventory @a4-resource-inventory-service
  Scenario: NEMO deletes non-existent TP (idempotency test)
    Given no TP exists in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202

  # X-Ray: DIGIHUB-127643
  @berlinium @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-commissioning
  Scenario: NEMO deletes TP without attached NSP, therefore deprovisioning to U-Piter is not triggered
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And no NSP FTTH exists in A4 resource inventory for the TP
    And the U-Piter DPU mock will respond HTTP code 202 when called
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And no DPU deprovisioning request to U-Piter was triggered
    And the TP does not exist in A4 resource inventory anymore
