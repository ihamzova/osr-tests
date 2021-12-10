@REG_DIGIHUB-118272
Feature: Berlinium parts of DPU Commissioning in A4 platform - Delete FTTH Access line with Deprovisioning

  # X-Ray: DIGIHUB-127854
  @berlinium @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-commissioning
  Scenario: NEMO deletes TP with NSP attached, therefore deprovisioning to U-Piter is triggered
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the U-Piter DPU mock will respond HTTP code 202 when called, and delete the NSP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And a DPU deprovisioning request to U-Piter DPU mock was triggered with Line ID "DEU.DTAG.12345"
    When the U-Piter DPU mock sends the callback
    Then the NSP FTTH does not exist in A4 resource inventory anymore
    And the TP does not exist in A4 resource inventory anymore

  # TODO: Create this scenario
  # DIGIHUB-121769, scenario #3
