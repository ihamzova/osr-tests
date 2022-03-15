Feature: [DIGIHUB-142105][Berlinium] Receiving Inventory from Plural

  Background:
    Given a user with Berlinium credentials

  @berlinium @domain @ui @heiko
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
  Scenario: Receive new NE from Plural for a non-existing Network Element Group on ui

    Given the plural mock will respond HTTP code 201 when called
    And delete neg in ri recursively
    And open import-ui
    When insert neg name
    Then positive response from importer at ui is received
    And ri was created with neg and ne and neps
    And update notifications was sent to NEMO
    #And delete neg in ri recursively












#  @berlinium @domain
 # @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
 # Scenario: create NEG in ri
    # neg will be deleted at the end of test
 #   Given a NEG with name "NEG-123456" is existing in A4 resource inventory