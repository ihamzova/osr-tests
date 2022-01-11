Feature: [DIGIHUB-xxxxx][Berlinium] Sending Update Calls to Nemo

  @berlinium
  @a4-resource-inventory @a4-nemo-updater @a4-queue-dispatcher
  Scenario: Trigger an update call to NEMO for an existing Network Element Group
    Given a NEG is existing in A4 resource inventory
    When an update call to NEMO for the NEG is triggered
    Then 1 "PUT" NEG update notification was sent to NEMO

  @berlinium
  @a4-resource-inventory @a4-nemo-updater @a4-queue-dispatcher
  Scenario: Trigger an update call to NEMO for a non-existing Network Element Group
    Given no NEG exists in A4 resource inventory
    When an update call to NEMO for the NEG is triggered
    Then 1 "DELETE" NEG update notification was sent to NEMO
