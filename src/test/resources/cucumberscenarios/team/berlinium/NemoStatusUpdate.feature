Feature: [DIGIHUB-xxxxx][Berlinium] Nemo Status Update Test

  @berlinium @domain
    @a4-resource-inventory @a4-resource-inventory-service
  Scenario Outline: NEMO sends a status patch for A4 Network Service Profile (L2BSA) Operational State property
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And a NSP L2BSA with operationalState "<OldOpState>" and lifecycleState "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to change NSP L2BSA operationalState to "<NewOpState>"
    Then the request is responded with HTTP code 201
    And the NSP L2BSA operationalState is "<NewOpState>"
    And the NSP L2BSA lifecycleState is "<NewLcState>"

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
