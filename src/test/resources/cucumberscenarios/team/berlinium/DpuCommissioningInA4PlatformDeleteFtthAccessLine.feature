@REG_DIGIHUB-118272
Feature: [DIGIHUB-118272][Berlinium] DPU Commissioning in A4 platform - Delete FTTH Accessline (part 3)
  # https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=396658649

  # X-Ray: DIGIHUB-127641
  @berlinium @domain
  @a4-resource-inventory @a4-resource-inventory-service
  Scenario: NEMO deletes non-existent Termination Point (idempotency test)
    Given no TP exists in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202

  # X-Ray: DIGIHUB-127643
  @berlinium
  @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-commissioning
  Scenario: NEMO deletes Termination Point without deprovisioning triggered
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And no NSP FTTH exists in A4 resource inventory for the TP
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code 202 when called
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And no DPU deprovisioning request to wg-a4-provisioning mock was triggered
    And the TP does not exist in A4 resource inventory anymore

  # X-Ray: DIGIHUB-127854
  @berlinium
  @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-commissioning
  Scenario: NEMO deletes Termination Point with deprovisioning triggered
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code 202 when called, and delete the NSP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And a DPU deprovisioning request to wg-a4-provisioning mock was triggered with Line ID "DEU.DTAG.12345"
    When the wg-a4-provisioning mock sends the callback
    Then the callback request is responded with HTTP code 200
    And the NSP FTTH does not exist in A4 resource inventory anymore
    And the TP does not exist in A4 resource inventory anymore

  # TODO: Create this scenario
  # DIGIHUB-121769, scenario #3

  # X-Ray: DIGIHUB-127642
  @berlinium @domain
  @a4-resource-inventory-service
  Scenario Outline: NEMO deletes Termination Point with invalid types
    Given a TP with type "<Type>" is existing in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP error code 400

    Examples:
      | Type      |
      | G_FAST_TP |
      | G.FAST_TP |
      | A10NSP_TP |
      | L2BSA_TP  |

  # DIGIHUB-121769, scenario #4, and #5
  @berlinium
  Scenario Outline: Triggered deprovisioning - U-Piter not reachable; retry
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code <HTTPCode> when called the 1st time
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code 202 when called the 2nd time, and delete the NSP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And a DPU deprovisioning request to wg-a4-provisioning mock was triggered with Line ID "DEU.DTAG.12345"
    And the DPU deprovisioning request to wg-a4-provisioning mock is repeated after 3 minutes
    When the wg-a4-provisioning mock sends the callback
    Then the callback request is responded with HTTP code 200
    And the NSP FTTH does not exist in A4 resource inventory anymore
    And the TP does not exist in A4 resource inventory anymore

    Examples:
      | HTTPCode |
      | 403      |
      | 408      |
      | 500      |

  # DIGIHUB-121769, scenario #6
  @berlinium
  Scenario Outline: Triggered deprovisioning - U-Piter not reachable; give up
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code <HTTPCode> when called
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And a DPU deprovisioning request to wg-a4-provisioning mock was triggered with Line ID "DEU.DTAG.12345"
    And the TP UUID is added to A4 deprovisioning DLQ

    Examples:
      | HTTPCode |
      | 400      |
      | 401      |

  # TODO Complete/fix this scenario
  # DIGIHUB-118971, scenario #4
#  @berlinium
#  Scenario Outline: Delete TP - A4 Resource Inventory not reachable
#    Given a TP with type "PON_TP" is existing in A4 resource inventory
#    And the A4 resource inventory will respond HTTP code <HTTPCode> when called
#    When the U-Piter DPU mock sends the callback
#    Then the request is responded with HTTP code 200
#
#    Examples:
#      | HTTPCode |
#      | 500      |

  # TODO
  # What happens when there's no NSP FTTH connected with the TP, but another NSP (A10NSP, L2BSA)?
