@DIGIHUB-90382 @DIGIHUB-144049
Feature: Nemo Status Update Test
  Some great description of what this feature is about.

  # ---------- PATCH NEG ----------

  @DIGIHUB-140170
    @team:berlinium @domain:osr @smoke
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
      | OldOpState     | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING    | INSTALLING | INSTALLING     | INSTALLING |
      | NOT_WORKING    | OPERATING  | INSTALLING     | OPERATING  |
      | NOT_WORKING    | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING    | RETIRING   | INSTALLING     | RETIRING   |
      | NOT_WORKING    | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING    | INSTALLING | WORKING        | OPERATING  |
      | INSTALLING     | INSTALLING | WORKING        | OPERATING  |
      | WORKING        | OPERATING  | NOT_MANAGEABLE | OPERATING  |
      | NOT_MANAGEABLE | OPERATING  | WORKING        | OPERATING  |
      | NOT_MANAGEABLE | OPERATING  | NOT_WORKING    | OPERATING  |

      # Invalid operational state value shall be accepted
      | NOT_WORKING    | PLANNING   | invalidOpState | PLANNING   |

      # Old values = new values; still counts as update
      | NOT_WORKING    | PLANNING   | NOT_WORKING    | PLANNING   |

      # Changed with DIGIHUB-80041:
      | NOT_WORKING    | PLANNING   | INSTALLING     | INSTALLING |

  @DIGIHUB-144194
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NEG without operational state characteristic
    Given a NEG with operational state "NOT_WORKING" and lifecycle state "PLANNING" is existing in A4 resource inventory
    When NEMO sends a request to update NEG without operationalState
    Then the request is responded with HTTP code 201
    And the NEG operationalState is still "NOT_WORKING"
    And the NEG lifecycleState is still "PLANNING"
    And the NEG lastUpdateTime is updated
    And 1 "PUT" NEG update notifications were sent to NEMO

  @DIGIHUB-144195
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NEG with _all_ properties changed to new values; only states are changed, everything else kept at old values
    Given a NEG with the following properties is existing in A4 resource inventory:
      | description                  | oldDesc                   |
      | centralOfficeNetworkOperator | oldNegOp                  |
      | specificationVersion         | oldSpecVer                |
      | lifecycleState               | oldLcState                |
      | lastSuccessfulSyncTime       | 2010-10-10T10:10:10+02:00 |
      | type                         | oldType                   |
      | creationTime                 | 2010-10-10T10:10:10+02:00 |
      | operationalState             | oldOpState                |
      | name                         | oldName                   |
      | lastUpdateTime               | 2010-10-10T10:10:10+02:00 |
    When NEMO sends a request to update the NEG's following properties to:
      | description                  | newDesc                   |
      | centralOfficeNetworkOperator | newNegOp                  |
      | specificationVersion         | newSpecVer                |
      | lifecycleState               | newLcState                |
      | lastSuccessfulSyncTime       | 2022-02-22T22:22:22+02:00 |
      | type                         | newType                   |
      | creationTime                 | 2022-02-22T22:22:22+02:00 |
      | operationalState             | newOpState                |
      | name                         | newName                   |
      | lastUpdateTime               | 2022-02-22T22:22:22+02:00 |
    Then the request is responded with HTTP code 201
    And the NEG now has the following properties:
      | description                  | oldDesc    |
      | centralOfficeNetworkOperator | oldNegOp   |
      | specificationVersion         | oldSpecVer |
      | lifecycleState               | oldLcState |
      | type                         | oldType    |
      | operationalState             | newOpState |
      | name                         | oldName    |
    And the NEG creationTime is not updated
    And the NEG lastUpdateTime is updated
    And the NEG lastSuccessfulSyncTime property was updated
    And 1 "PUT" NEG update notification was sent to NEMO


 # ---------- PATCH NE ----------

  @DIGIHUB-144196
    @team:berlinium @domain:osr
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
      | OldOpState     | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING    | INSTALLING | INSTALLING     | INSTALLING |
      | NOT_WORKING    | OPERATING  | INSTALLING     | OPERATING  |
      | NOT_WORKING    | RETIRING   | INSTALLING     | RETIRING   |
      | NOT_WORKING    | PLANNING   | WORKING        | OPERATING  |
      | NOT_WORKING    | INSTALLING | WORKING        | OPERATING  |
      | INSTALLING     | INSTALLING | WORKING        | OPERATING  |

      # Invalid operational state value and FAILED shall be accepted
      | NOT_WORKING    | PLANNING   | invalidOpState | PLANNING   |
      | invalidOpState | INSTALLING | WORKING        | OPERATING  |

      # Old values = new values; still counts as update
      | NOT_WORKING    | PLANNING   | NOT_WORKING    | PLANNING   |

  @DIGIHUB-144197
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NE without operational state characteristic
    Given a NE with operational state "NOT_WORKING" and lifecycle state "PLANNING" is existing in A4 resource inventory
    When NEMO sends a request to update NE without operationalState
    Then the request is responded with HTTP code 201
    And the NE operationalState is still "NOT_WORKING"
    And the NE lifecycleState is still "PLANNING"
    And the NE lastUpdateTime is updated
    And 1 "PUT" NE update notifications were sent to NEMO

  # Add DIGIHUB-XXX after export to Jira
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NE with _all_ properties changed to new values; only states are changed, everything else kept at old values
    Given a NE with the following properties is existing in A4 resource inventory:
      | description                  | oldDesc                   |
      | address                      | oldAddress                |
      | specificationVersion         | oldSpecVer                |
      | lifecycleState               | oldLcState                |
      | plannedRackId                | oldPlannedRackId          |
      | plannedRackPosition          | oldPlannedRackPosition    |
      | planningDeviceName           | oldPlanningDeviceName     |
      | plannedMatNumber             | 111111111                 |
      | type                         | oldType                   |
      | roles                        | oldRoles                  |
      | operationalState             | oldOpState                |
      | administrativeState          | oldAdminState             |
      | klsId                        | 1111111                   |
      | fiberOnLocationId            | 11111111                  |
    When NEMO sends a request to update the NE's following properties to:
      | description                  | newDesc                   |
      | address                      | newAddress                |
      | specificationVersion         | newSpecVer                |
      | lifecycleState               | newLcState                |
      | plannedRackId                | newPlannedRackId          |
      | plannedRackPosition          | newPlannedRackPosition    |
      | planningDeviceName           | newPlanningDeviceName     |
      | plannedMatNumber             | 222222222                 |
      | type                         | newType                   |
      | roles                        | newRoles                  |
      | operationalState             | newOpState                |
      | administrativeState          | newAdminState             |
      | klsId                        | 2222222                   |
      | fiberOnLocationId            | 22222222                  |
    Then the request is responded with HTTP code 201
    And the NE now has the following properties:
      | description                  | oldDesc                   |
      | address                      | oldAddress                |
      | specificationVersion         | oldSpecVer                |
      | lifecycleState               | oldLcState                |
      | plannedRackId                | oldPlannedRackId          |
      | plannedRackPosition          | oldPlannedRackPosition    |
      | planningDeviceName           | oldPlanningDeviceName     |
      | plannedMatNumber             | 111111111                 |
      | type                         | oldType                   |
      | roles                        | oldRoles                  |
      | operationalState             | newOpState                |
      | administrativeState          | oldAdminState             |
      | klsId                        | 1111111                   |
      | fiberOnLocationId            | 11111111                  |
    And the NE creationTime is not updated
    And the NE lastUpdateTime is updated
    And the NE lastSuccessfulSyncTime property was updated
    And 1 "PUT" NE update notification was sent to NEMO


  # ---------- PATCH NEP ----------

  @DIGIHUB-144198
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port
    Given a NEP with operational state "NOT_WORKING" and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP operationalState to "INSTALLING" and description to "newDescr"
    Then the request is responded with HTTP code 201
    And the NEP operationalState is updated to "INSTALLING"
    And the NEP description is updated to "newDescr"
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  @DIGIHUB-144199
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port without operational state
    Given a NEP with operational state "NOT_WORKING" and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP description to "newDescr"
    Then the request is responded with HTTP code 201
    And the NEP operationalState is still "NOT_WORKING"
    And the NEP description is updated to "newDescr"
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  # Add @DIGIHUB-xxxx if exported
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port with empty description
    Given a NEP with operational state "NOT_WORKING" and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP description to ""
    Then the request is responded with HTTP code 201
    And the NEP description is updated to ""
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  @DIGIHUB-144200
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port without description
    Given a NEP with operational state "NOT_WORKING" and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP operational state to "WORKING"
    Then the request is responded with HTTP code 201
    And the NEP operationalState is updated to "WORKING"
    And the NEP description is still "OldDescr"
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  @DIGIHUB-144202
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Element Port without operationalState nor description
    Given a NEP with operational state "NOT_WORKING" and description "OldDescr" is existing in A4 resource inventory
    When NEMO sends a request to update NEP without operationalState nor description
    Then the request is responded with HTTP code 201
    And the NEP operationalState is still "NOT_WORKING"
    And the NEP description is still "OldDescr"
    And the NEP lastUpdateTime is updated
    And 1 "PUT" NEP update notification was sent to NEMO

  # Add scenario that only opState / description is patched, everything else not


  # ---------- PATCH NEL ----------

  @DIGIHUB-144203
    @team:berlinium @domain:osr
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

  @DIGIHUB-144204
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NEL without operational state characteristic
    Given a NEL with operational state "NOT_WORKING" and lifecycle state "PLANNING" is existing in A4 resource inventory
    When NEMO sends a request to update NEL without operationalState
    Then the request is responded with HTTP code 201
    And the NEL operationalState is still "NOT_WORKING"
    And the NEL lifecycleState is still "PLANNING"
    And the NEL lastUpdateTime is updated
    And 1 "PUT" NEL update notifications were sent to NEMO

  # Add scenario that only opState is patched, everything else is ignored


  # ---------- PATCH NSP FTTH-Access ----------

  @DIGIHUB-144617
    @team:berlinium @domain:osr
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario Outline: NEMO sends a status patch for A4 Network Service Profile (FTTH-Access)
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH-Access with operationalState "<OldOpState>" and lifecycleState "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to change NSP FTTH-Access operational state to "<NewOpState>"
    Then the request is responded with HTTP code 201
    And the NSP FTTH-Access operationalState is updated to "<NewOpState>"
    And the NSP FTTH-Access lifecycleState is updated to "<NewLcState>"
    And the NSP FTTH-Access lastUpdateTime is updated
    And 1 "PUT" NSP FTTH update notification was sent to NEMO

    Examples:
      | OldOpState   | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING  | PLANNING   | ACTIVATING     | PLANNING   |
      | ACTIVATING   | PLANNING   | WORKING        | OPERATING  |
      | ACTIVATING   | INSTALLING | WORKING        | OPERATING  |
      | ACTIVATING   | OPERATING  | WORKING        | OPERATING  |
      | ACTIVATING   | PLANNING   | FAILED         | PLANNING   |
      | FAILED       | PLANNING   | DEACTIVATING   | PLANNING   |
      | DEACTIVATING | PLANNING   | NOT_WORKING    | PLANNING   |
      | DEACTIVATING | PLANNING   | FAILED         | PLANNING   |
      | FAILED       | PLANNING   | WORKING        | OPERATING  |

      # Old values = new values; still counts as update
      | NOT_WORKING  | PLANNING   | NOT_WORKING    | PLANNING   |

      # X-Ray: DIGIHUB-94384: Invalid operational state value shall be accepted
      | NOT_WORKING  | PLANNING   | invalidOpState | PLANNING   |

  @DIGIHUB-144205
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Service Profile FTTH-Access with both NEP reference and operational state
    Given a NSP FTTH-Access with operational state "WORKING" and NEP reference "oldPortUuid" is existing in A4 resource inventory
    When NEMO sends a request to update NSP FTTH-Access operationalState to "ACTIVATING" and NEP reference to "newPortUuid"
    Then the request is responded with HTTP code 201
    And the NSP FTTH-Access operationalState is updated to "ACTIVATING"
    # -- NemoStatusUpdate does not check, if NEP refernce is a real existing ONT- Port
    And the NSP FTTH-Access NEP reference is updated to "newPortUuid"
    And the NSP FTTH-Access lastUpdateTime is updated
    And 1 "PUT" NSP FTTH update notification was sent to NEMO

  @DIGIHUB-144206
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Service Profile FTTH-Access without operational state
    Given a NSP FTTH-Access with operational state "NOT_WORKING" and NEP reference "oldPortUuid" is existing in A4 resource inventory
    When NEMO sends a request to update NSP FTTH-Access NEP reference to "newPortUuid"
    Then the request is responded with HTTP code 201
    And the NSP FTTH-Access operationalState is still "NOT_WORKING"
    # -- NemoStatusUpdate does not check, if NEP refernce is a real existing ONT- Port
    And the NSP FTTH-Access NEP reference is updated to "newPortUuid"
    And the NSP FTTH-Access lastUpdateTime is updated
    And 1 "PUT" NSP FTTH update notification was sent to NEMO

  @DIGIHUB-144207
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Service Profile FTTH-Access without NEP reference
    Given a NSP FTTH-Access with operational state "NOT_WORKING" and NEP reference "oldPortUuid" is existing in A4 resource inventory
    When NEMO sends a request to update NSP FTTH-Access operational state to "WORKING"
    Then the request is responded with HTTP code 201
    And the NSP FTTH-Access operationalState is updated to "WORKING"
    And the NSP FTTH-Access NEP reference is still "oldPortUuid"
    And the NSP FTTH-Access lastUpdateTime is updated
    And 1 "PUT" NSP FTTH update notification was sent to NEMO

  @DIGIHUB-144208
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 Network Service Profile FTTH-Access without operationalState nor NEP reference
    Given a NSP FTTH-Access with operational state "NOT_WORKING" and NEP reference "oldPortUuid" is existing in A4 resource inventory
    When NEMO sends a request to update NSP FTTH-Access without operationalState nor NEP reference
    Then the request is responded with HTTP code 201
    And the NSP FTTH-Access operationalState is still "NOT_WORKING"
    And the NSP FTTH-Access NEP reference is still "oldPortUuid"
    And the NSP FTTH-Access lastUpdateTime is updated
    And 1 "PUT" NSP FTTH update notification was sent to NEMO

  # Add scenario that only opState / oltPortOntLastRegisteredOn is patched, everything else not


  # ---------- PATCH NSP L2BSA ----------

  @DIGIHUB-144209
    @team:berlinium @domain:osr
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
      | OldOpState   | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING  | PLANNING   | ACTIVATING     | PLANNING   |
      | ACTIVATING   | PLANNING   | WORKING        | OPERATING  |
      | ACTIVATING   | INSTALLING | WORKING        | OPERATING  |
      | ACTIVATING   | OPERATING  | WORKING        | OPERATING  |
      | ACTIVATING   | RETIRING   | WORKING        | RETIRING   |
      | ACTIVATING   | PLANNING   | FAILED         | PLANNING   |
      | FAILED       | PLANNING   | DEACTIVATING   | PLANNING   |
      | DEACTIVATING | PLANNING   | NOT_WORKING    | PLANNING   |
      | DEACTIVATING | PLANNING   | FAILED         | PLANNING   |
      | FAILED       | PLANNING   | WORKING        | OPERATING  |

      # Old values = new values; still counts as update
      | NOT_WORKING  | PLANNING   | NOT_WORKING    | PLANNING   |

      # X-Ray: DIGIHUB-94384: Invalid operational state value shall be accepted
      | NOT_WORKING  | PLANNING   | invalidOpState | PLANNING   |

  @DIGIHUB-144210
  @team:berlinium @domain:osr
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

  # Add scenario that only opState is patched, everything else not


  # ---------- PATCH NSP A10NSP ---------

  @DIGIHUB-144211
    @team:berlinium @domain:osr
    @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario Outline: NEMO sends a status patch for A4 Network Service Profile (A10NSP)
    Given a TP with type "A10NSP_TP" is existing in A4 resource inventory
    And a NSP A10NSP with operationalState "<OldOpState>" and lifecycleState "<OldLcState>" is existing in A4 resource inventory
    When NEMO sends a request to change NSP A10NSP operationalState to "<NewOpState>"
    Then the request is responded with HTTP code 201
    And the NSP A10NSP operationalState is updated to "<NewOpState>"
    And the NSP A10NSP lifecycleState is updated to "<NewLcState>"
    And the NSP A10NSP lastUpdateTime is updated
    And 1 "PUT" NSP A10NSP update notification was sent to NEMO

    Examples:
      | OldOpState   | OldLcState | NewOpState     | NewLcState |
      | NOT_WORKING  | PLANNING   | ACTIVATING     | PLANNING   |
      | ACTIVATING   | PLANNING   | WORKING        | OPERATING  |
      | ACTIVATING   | INSTALLING | WORKING        | OPERATING  |
      | ACTIVATING   | OPERATING  | WORKING        | OPERATING  |
      | ACTIVATING   | RETIRING   | WORKING        | RETIRING   |
      | ACTIVATING   | PLANNING   | FAILED         | PLANNING   |
      | FAILED       | PLANNING   | DEACTIVATING   | PLANNING   |
      | DEACTIVATING | PLANNING   | NOT_WORKING    | PLANNING   |
      | DEACTIVATING | PLANNING   | FAILED         | PLANNING   |
      | FAILED       | PLANNING   | WORKING        | OPERATING  |

      # Old values = new values; still counts as update
      | NOT_WORKING  | PLANNING   | NOT_WORKING    | PLANNING   |

      # X-Ray: DIGIHUB-94384: Invalid operational state value shall be accepted
      | NOT_WORKING  | PLANNING   | invalidOpState | PLANNING   |

  @DIGIHUB-144212
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: NEMO sends a status patch for A4 NSP A10NSP without operational state characteristic
    Given a TP with type "A10NSP_TP" is existing in A4 resource inventory
    And a NSP A10NSP with operationalState "NOT_WORKING" and lifecycleState "PLANNING" is existing in A4 resource inventory
    When NEMO sends a request to update NSP A10NSP without operationalState
    Then the request is responded with HTTP code 201
    And the NSP A10NSP operationalState is still "NOT_WORKING"
    And the NSP A10NSP lifecycleState is still "PLANNING"
    And the NSP A10NSP lastUpdateTime is updated
    And 1 "PUT" NSP A10NSP update notifications were sent to NEMO

  # Add scenario that only opState is patched, everything else not
