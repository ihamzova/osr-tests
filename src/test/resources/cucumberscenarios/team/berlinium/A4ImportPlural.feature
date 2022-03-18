@DIGIHUB-117772
Feature: Receiving Inventory from Plural
  Some great description of what this feature is about.

  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
  Scenario: Receive new NE from Plural for a non-existing Network Element Group

    Given the plural mock will respond HTTP code 201 when called
    And delete neg in ri recursively
    When trigger auto-import request to importer
    Then positive response from importer received
    And ri was created with neg and ne and neps
    And update notifications was sent to NEMO
