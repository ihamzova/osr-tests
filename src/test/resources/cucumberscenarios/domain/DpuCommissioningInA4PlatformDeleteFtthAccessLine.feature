Feature: [DIGIHUB-118272][OS&R] DPU Commissioning in A4 platform - Delete FTTH Accessline (part 3)
  # https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=396658649

  # X-Ray: DIGIHUB-132266
  @domain
  @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-commissioning
  Scenario: NEMO deletes Termination Point with deprovisioning triggered
    Given a DPU preprovisioning was done earlier
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    # --- Add any U-Piter Then-steps here ---
    And the NSP FTTH does not exist in A4 resource inventory anymore
    And 1 "DELETE" NSP FTTH update notification was sent to NEMO
    And the TP does not exist in A4 resource inventory anymore
