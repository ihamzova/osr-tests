@DIGIHUB-118272
Feature: DPU Commissioning in A4 platform - Delete FTTH Accessline (part 3)
  See https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=396658649 for details.

  @DIGIHUB-127641
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
  Scenario: NEMO deletes non-existent Termination Point (idempotency test)
    Given no TP exists in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202

  @DIGIHUB-127643
  @team:berlinium @smoke
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-queue-dispatcher @ms:a4-commissioning
  Scenario: NEMO deletes Termination Point without deprovisioning triggered
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And no NSP FTTH exists in A4 resource inventory for the TP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And no DPU deprovisioning request to wg-a4-provisioning mock was triggered
    And the TP does not exist in A4 resource inventory anymore

  @DIGIHUB-127854
  @team:berlinium @smoke
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-queue-dispatcher @ms:a4-commissioning
  Scenario: NEMO deletes Termination Point with deprovisioning triggered
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code 202 when called, and delete the NSP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And 1 DPU deprovisioning request to wg-a4-provisioning mock was triggered with Line ID "DEU.DTAG.12345"
    When the wg-a4-provisioning mock sends the callback
    Then the callback request is responded with HTTP code 200
    And the NSP FTTH does not exist in A4 resource inventory anymore
    And the TP does not exist in A4 resource inventory anymore

  # TODO: Create this scenario
  # DIGIHUB-121769, scenario #3

  @DIGIHUB-127642
    @team:berlinium @domain:osr
    @ms:a4-resource-inventory-service
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

  @DIGIHUB-144191
    @team:berlinium
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-queue-dispatcher @ms:a4-commissioning
  Scenario Outline: Triggered deprovisioning - U-Piter not reachable; retry (Decoupling Component gives up instantly)
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code <HTTPCode> when called the 1st time
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code 202 when called the 2nd time, and delete the NSP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And 1 DPU deprovisioning request to wg-a4-provisioning mock was triggered with Line ID "DEU.DTAG.12345"
    And the DPU deprovisioning request to wg-a4-provisioning mock is repeated after 3 minutes
    When the wg-a4-provisioning mock sends the callback
    Then the NSP FTTH does not exist in A4 resource inventory anymore
    And the TP does not exist in A4 resource inventory anymore

    Examples:
      | HTTPCode |
      | 403      |
      | 408      |

  @DIGIHUB-144192
    @team:berlinium
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-queue-dispatcher @ms:a4-commissioning
  Scenario Outline: Triggered deprovisioning - U-Piter not reachable; retry (Decoupling Component retries 2 times)
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code <HTTPCode> when called the first 3 times
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code 202 when called the 4th time, and delete the NSP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And 3 DPU deprovisioning requests to wg-a4-provisioning mock were triggered with Line ID "DEU.DTAG.12345"
    And the DPU deprovisioning request to wg-a4-provisioning mock is repeated after 3 minutes
    When the wg-a4-provisioning mock sends the callback
    Then the NSP FTTH does not exist in A4 resource inventory anymore
    And the TP does not exist in A4 resource inventory anymore

    Examples:
      | HTTPCode |
      | 503      |

  @DIGIHUB-144193
    @team:berlinium
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-queue-dispatcher @ms:a4-commissioning
  Scenario Outline: Triggered deprovisioning - U-Piter not reachable; give up
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the wg-a4-provisioning deprovisioning mock will respond HTTP code <HTTPCode> when called
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And 1 DPU deprovisioning request to wg-a4-provisioning mock was triggered with Line ID "DEU.DTAG.12345"
    And the TP UUID is added to A4 deprovisioning DLQ

    Examples:
      | HTTPCode |
      | 400      |
      | 501      |

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
