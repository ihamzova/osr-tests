@DIGIHUB-129091
Feature: Sending Update Calls to Nemo
  Some great description of what this feature is about.

  @DIGIHUB-144189
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-nemo-updater
  Scenario: Trigger an update call to NEMO for an existing Network Element Group
    Given a NEG is existing in A4 resource inventory
    When an update call to NEMO for the NEG is triggered
    Then 1 "PUT" NEG update notification was sent to NEMO
    And the NEG lastSuccessfulSyncTime property was updated

  #@DIGIHUB-xxxxxx
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: Trigger an async update call to NEMO for an existing Network Element Group
    Given a NEG is existing in A4 resource inventory
    When an async update call to NEMO for the NEG is triggered
    Then 1 "PUT" NEG update notification was sent to NEMO
    And the NEG lastSuccessfulSyncTime property was updated

  @DIGIHUB-144190
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-nemo-updater
  Scenario: Trigger an update call to NEMO for a non-existing Network Element Group
    Given no NEG exists in A4 resource inventory
    When an update call to NEMO for the NEG is triggered
    Then 1 "DELETE" NEG update notification was sent to NEMO

  #@DIGIHUB-xxxxxx
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-queue-dispatcher
  Scenario: Trigger an async update call to NEMO for a non-existing Network Element Group
    Given no NEG exists in A4 resource inventory
    When an async update call to NEMO for the NEG is triggered
    Then 1 "DELETE" NEG update notification was sent to NEMO
