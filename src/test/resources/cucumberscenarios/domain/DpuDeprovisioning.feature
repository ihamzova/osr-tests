Feature: OSR Domain test of DPU Commissioning in A4 platform - Delete FTTH Access line with Deprovisioning

  @domain @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-commissioning
  Scenario: (DOMAIN) NEMO deletes TP with NSP attached, including deprovisioning on U-Piter side
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    # --- Add any U-Piter Given-steps here: ---
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    # --- Add any U-Piter Then-steps here: ---
    And a delete NSP FTTH update notification was sent to NEMO
    And the NSP FTTH does not exist in A4 resource inventory anymore
    And the TP does not exist in A4 resource inventory anymore
