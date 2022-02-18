Feature: [DIGIHUB-xxxxx][DIGIHUB-90382][Berlinium] Nemo Status Update Test

  @berlinium @domain @smoke
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service
  Scenario Outline: NEMO sends a status patch for A4 Network Element Group
    Given a NEG with operational state "<OldOpState>" and lifecycle state "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to update NEG operationalState to "<NewOpState>"
    Then the NEG operationalState is updated to "<NewOpState>"
    And the NEG lifecycleState is updated to "<NewLcState>"
    And 1 "PUT" NEG update notification was sent to NEMO

    Examples:
      | OldOpState  | OldLcState | NewOpState | NewLcState |
      # Changed with DIGIHUB-80041:
      | NOT_WORKING | PLANNING   | INSTALLING | INSTALLING |
      | NOT_WORKING | INSTALLING | INSTALLING | INSTALLING |
      | NOT_WORKING | OPERATING  | INSTALLING | OPERATING  |
      | NOT_WORKING | RETIRING   | INSTALLING | RETIRING   |

  @berlinium @domain
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service
  Scenario Outline: NEMO sends a status patch for A4 Network Service Profile (L2BSA)
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And a NSP L2BSA with operationalState "<OldOpState>" and lifecycleState "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to change NSP L2BSA operationalState to "<NewOpState>"
    Then the request is responded with HTTP code 201
    And the NSP L2BSA operationalState is updated to "<NewOpState>"
    And the NSP L2BSA lifecycleState is updated to "<NewLcState>"
    And 1 "PUT" NSP L2BSA update notification was sent to NEMO

    Examples:
      | OldOpState  | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING | INSTALLING | WORKING        | OPERATING  |
      | NOT_WORKING | OPERATING  | WORKING        | OPERATING  |
      | NOT_WORKING | RETIRING   | WORKING        | RETIRING   |
      | NOT_WORKING | PLANNING   | INSTALLING     | PLANNING   |
      | NOT_WORKING | PLANNING   | NOT_WORKING    | PLANNING   |
      | NOT_WORKING | PLANNING   | NOT_MANAGEABLE | PLANNING   |
      | NOT_WORKING | PLANNING   | FAILED         | PLANNING   |
      | NOT_WORKING | PLANNING   | ACTIVATING     | PLANNING   |
      | NOT_WORKING | PLANNING   | DEACTIVATING   | PLANNING   |
      # X-Ray: DIGIHUB-94384: Invalid operational state value shall be accepted
      | NOT_WORKING | PLANNING   | invalidOpState | PLANNING   |
