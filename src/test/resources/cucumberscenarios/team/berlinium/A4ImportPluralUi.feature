@DIGIHUB-117772
Feature: Receiving Inventory from Plural
  Some great description of what this feature is about.

  #@DIGIHUB-xxxxx
  Background:
    Given a user with Berlinium credentials

  @team:berlinium @domain:osr @ui
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
  Scenario: Receive new NE from Plural for a non-existing Network Element Group on ui

    Given the plural mock will respond HTTP code 201 when called
    And delete neg in ri recursively
    When open import-ui
    And insert neg name
    Then positive response from importer at ui is received
    And ri was created with neg and ne and neps
    And update notifications was sent to NEMO
