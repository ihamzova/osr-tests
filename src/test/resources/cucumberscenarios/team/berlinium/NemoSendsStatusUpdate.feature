Feature: [DIGIHUB-xxxxx][DIGIHUB-90382][Berlinium] Nemo Status Update Test

  # ---------- PATCH NEG ----------

  # X-Ray: DIGIHUB-140170
  @berlinium @domain @smoke
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario Outline: NEMO sends a status patch for A4 Network Element Group
    Given a NEG with operational state "<OldOpState>" and lifecycle state "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to update NEG operationalState to "<NewOpState>"
    Then the request is responded with HTTP code 201
    And the NEG operationalState is updated to "<NewOpState>"
    And the NEG lifecycleState is updated to "<NewLcState>"
    And the NEG lastUpdateTime is updated
    And 1 "PUT" NEG update notification was sent to NEMO

    Examples:
      | OldOpState  | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING | INSTALLING | INSTALLING     | INSTALLING |
      | NOT_WORKING | OPERATING  | INSTALLING     | OPERATING  |
      | NOT_WORKING | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING | RETIRING   | INSTALLING     | RETIRING   |
      | NOT_WORKING | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING | INSTALLING | WORKING        | OPERATING  |
      | INSTALLING  | INSTALLING | WORKING        | OPERATING  |

      # Invalid operational state value shall be accepted
      | NOT_WORKING | PLANNING   | invalidOpState | PLANNING   |

      # Old values = new values; still counts as update
      | NOT_WORKING | PLANNING   | NOT_WORKING    | PLANNING   |

      # Changed with DIGIHUB-80041:
      | NOT_WORKING | PLANNING   | INSTALLING     | INSTALLING |

  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NEG without operational state characteristic
    Given a NEG with operational state "NOT_WORKING" and lifecycle state "PLANNING" is existing in A4 resource inventory
    When NEMO sends a request to update NEG without operationalState
    Then the request is responded with HTTP code 201
    And the NEG operationalState is still "NOT_WORKING"
    And the NEG lifecycleState is still "PLANNING"
    And the NEG lastUpdateTime is updated
    And 1 "PUT" NEG update notifications were sent to NEMO

 # TODO: Add scenario that only opState is patched, everything else not


 # ---------- PATCH NE ----------

  @berlinium @domain
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario Outline: NEMO sends a status patch for A4 Network Element
    Given a NE with operational state "<OldOpState>" and lifecycle state "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to update NE operationalState to "<NewOpState>"
    Then the request is responded with HTTP code 201
    And the NE operationalState is updated to "<NewOpState>"
    And the NE lifecycleState is updated to "<NewLcState>"
    And the NE lastUpdateTime is updated
    And 1 "PUT" NE update notification was sent to NEMO

    Examples:
      | OldOpState  | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING | INSTALLING | INSTALLING     | INSTALLING |
      | NOT_WORKING | OPERATING  | INSTALLING     | OPERATING  |
      | NOT_WORKING | RETIRING   | INSTALLING     | RETIRING   |
      | NOT_WORKING | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING | INSTALLING | WORKING        | OPERATING  |
      | INSTALLING  | INSTALLING | WORKING        | OPERATING  |
      # Invalid operational state value shall be accepted
      | NOT_WORKING | PLANNING   | invalidOpState | PLANNING   |
      # Old values = new values; still counts as update
      | NOT_WORKING | PLANNING   | NOT_WORKING    | PLANNING   |


  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NE without operational state characteristic
    Given a NE with operational state "NOT_WORKING" and lifecycle state "PLANNING" is existing in A4 resource inventory
    When NEMO sends a request to update NE without operationalState
    Then the request is responded with HTTP code 201
    And the NE operationalState is still "NOT_WORKING"
    And the NE lifecycleState is still "PLANNING"
    And the NE lastUpdateTime is updated
    And 1 "PUT" NE update notifications were sent to NEMO

  # TODO: Add scenario that only opState is patched, everything else is ignored


  # ---------- PATCH NEP ----------

  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port
    Given a NEP with operational state "NOT_WORKING" and and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP operationalState to "INSTALLING" and description to "newDescr"
    Then the request is responded with HTTP code 201
    And the NEP operationalState is updated to "INSTALLING"
    And the NEP description is updated to "newDescr"
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port without operational state
    Given a NEP with operational state "NOT_WORKING" and and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP description to "newDescr"
    Then the request is responded with HTTP code 201
    And the NEP operationalState is still "NOT_WORKING"
    And the NEP description is updated to "newDescr"
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port without description
    Given a NEP with operational state "NOT_WORKING" and and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP operational state to "WORKING"
    Then the request is responded with HTTP code 201
    And the NEP operationalState is updated to "WORKING"
    And the NEP description is deleted
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port without operationalState nor description
    Given a NEP with operational state "NOT_WORKING" and and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP without operationalState nor description
    Then the request is responded with HTTP code 201
    And the NEP operationalState is still "NOT_WORKING"
    And the NEP description is deleted
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  # TODO: Add scenario that only opState / description is patched, everything else not


  # ---------- PATCH NEL ----------

  @berlinium @domain
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario Outline: NEMO sends a status patch for A4 Network Element Link
    Given a NEL with operational state "<OldOpState>" and lifecycle state "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to update NEL operationalState to "<NewOpState>"
    Then the request is responded with HTTP code 201
    And the NEL operationalState is updated to "<NewOpState>"
    And the NEL lifecycleState is updated to "<NewLcState>"
    And the NEL lastUpdateTime is updated
    And 1 "PUT" NEL update notification was sent to NEMO

    Examples:
      | OldOpState  | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING | INSTALLING | INSTALLING     | INSTALLING |
      | NOT_WORKING | OPERATING  | INSTALLING     | OPERATING  |
      | NOT_WORKING | RETIRING   | INSTALLING     | RETIRING   |
      | NOT_WORKING | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING | INSTALLING | WORKING        | OPERATING  |
      | INSTALLING  | INSTALLING | WORKING        | OPERATING  |
      # Invalid operational state value shall be accepted
      | NOT_WORKING | PLANNING   | invalidOpState | PLANNING   |
      # Old values = new values; still counts as update
      | NOT_WORKING | PLANNING   | NOT_WORKING    | PLANNING   |


  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NEL without operational state characteristic
    Given a NEL with operational state "NOT_WORKING" and lifecycle state "PLANNING" is existing in A4 resource inventory
    When NEMO sends a request to update NEL without operationalState
    Then the request is responded with HTTP code 201
    And the NEL operationalState is still "NOT_WORKING"
    And the NEL lifecycleState is still "PLANNING"
    And the NEL lastUpdateTime is updated
    And 1 "PUT" NEL update notifications were sent to NEMO

  # TODO: Add scenario that only opState is patched, everything else is ignored



  # ---------- PATCH NSP FTTH-Access ----------

  # TODO :Add scenarios for PATCH NSP FTTH-ACCESS (equivalent to NEP, but use ontLastRegisteredOn instead of description)


  # ---------- PATCH NSP L2BSA ----------

  @berlinium @domain
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario Outline: NEMO sends a status patch for A4 Network Service Profile (L2BSA)
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And a NSP L2BSA with operationalState "<OldOpState>" and lifecycleState "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to change NSP L2BSA operationalState to "<NewOpState>"
    Then the request is responded with HTTP code 201
    And the NSP L2BSA operationalState is updated to "<NewOpState>"
    And the NSP L2BSA lifecycleState is updated to "<NewLcState>"
    And the NSP L2BSA lastUpdateTime is updated
    And 1 "PUT" NSP L2BSA update notification was sent to NEMO

    Examples:
      | OldOpState  | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING | INSTALLING | WORKING        | OPERATING  |
      | NOT_WORKING | OPERATING  | WORKING        | OPERATING  |
      | NOT_WORKING | RETIRING   | WORKING        | RETIRING   |
      | NOT_WORKING | PLANNING   | INSTALLING     | PLANNING   |
      | NOT_WORKING | PLANNING   | NOT_MANAGEABLE | PLANNING   |
      | NOT_WORKING | PLANNING   | FAILED         | PLANNING   |
      | NOT_WORKING | PLANNING   | ACTIVATING     | PLANNING   |
      | NOT_WORKING | PLANNING   | DEACTIVATING   | PLANNING   |

      # Old values = new values; still counts as update
      | NOT_WORKING | PLANNING   | NOT_WORKING    | PLANNING   |

      # X-Ray: DIGIHUB-94384: Invalid operational state value shall be accepted
      | NOT_WORKING | PLANNING   | invalidOpState | PLANNING   |

  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NSP L2BSA without operational state characteristic
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And a NSP L2BSA with operationalState "NOT_WORKING" and lifecycleState "PLANNING" is existing in A4 resource inventory
    When NEMO sends a request to update NSP L2BSA without operationalState
    Then the request is responded with HTTP code 201
    And the NSP L2BSA operationalState is still "NOT_WORKING"
    And the NSP L2BSA lifecycleState is still "PLANNING"
    And the NSP L2BSA lastUpdateTime is updated
    And 1 "PUT" NSP L2BSA update notifications were sent to NEMO

  # TODO: Add scenario that only opState is patched, everything else not


  # ---------- PATCH NSP A10NSP ---------

  # TODO: Add scenarios for NSP A10NSP (equivalent to NSP L2BSA)
