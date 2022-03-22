@DIGIHUB-118272
Feature: DPU Commissioning in A4 platform - Delete FTTH Accessline (part 3)
  See https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=396658649 for details.

  @DIGIHUB-132266
  @domain:osr @smoke
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-queue-dispatcher @ms:a4-commissioning
  Scenario: NEMO deletes Termination Point with deprovisioning triggered
    Given a DPU preprovisioning was done earlier
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    # --- Add any U-Piter Then-steps here ---
    And the NSP FTTH does not exist in A4 resource inventory anymore
    And 1 "DELETE" NSP FTTH update notification was sent to NEMO
    And the TP does not exist in A4 resource inventory anymore
