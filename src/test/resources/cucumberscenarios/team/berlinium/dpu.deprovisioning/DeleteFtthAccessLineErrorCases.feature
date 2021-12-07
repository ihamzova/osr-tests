@REG_DIGIHUB-118272
Feature: Berlinium parts of DPU Commissioning in A4 platform - Rainy day cases

  # X-Ray: DIGIHUB-127642
  @berlinium @domain @a4-resource-inventory-service
  Scenario Outline: NEMO deletes TP with invalid types
    Given a TP with type "<Type>" is existing in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP error code 400

    Examples:
      | Type      |
      | G_FAST_TP |
      | G.FAST_TP |
      | A10NSP_TP |
      | L2BSA_TP  |
      # NOTE: What is the currently official type name? G_FAST_TP or G.FAST_TP?

  # DIGIHUB-121769, scenario #4, and #5
  Scenario Outline: trigger deprovisioning - U-Piter not reachable; retry
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the U-Piter DPU mock will respond HTTP code <HTTPCode> when called the 1st time
    And the U-Piter DPU mock will respond HTTP code 202 when called the 2nd time, and delete the NSP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And a DPU deprovisioning request to U-Piter was triggered with Line ID "DEU.DTAG.12345"
    When the deprovisioning request to U-Piter is repeated after 3 minutes
    And the U-Piter DPU mock sends the callback
    Then the TP does not exist in A4 resource inventory anymore

    Examples:
      | HTTPCode |
      | 403      |
      | 408      |
      | 500      |

  # DIGIHUB-121769, scenario #6
  Scenario Outline: trigger deprovisioning - U-Piter not reachable; give up
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And the U-Piter DPU mock will respond HTTP code <HTTPCode> when called
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And a DPU deprovisioning request to U-Piter was triggered with Line ID "DEU.DTAG.12345"
    And the TP UUID is added to Deprovisioning DLQ

    Examples:
      | HTTPCode |
      | 400      |
      | 401      |

  # TODO Complete/fix this scenario
  # DIGIHUB-118971, scenario #4
  Scenario Outline: Delete TP - A4 Resource Inventory not reachable
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And the A4 resource inventory will respond HTTP code <HTTPCode> when called
    When the U-Piter DPU mock sends the callback
    Then the request is responded with HTTP code 200

    Examples:
      | HTTPCode |
      | 500      |
