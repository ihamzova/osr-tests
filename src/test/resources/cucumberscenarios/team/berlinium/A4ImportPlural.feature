Feature: [DIGIHUB-xxxxx][Berlinium] Receiving Inventory from Plural

  #Background:
  #  Given a user with Berlinium credentials

 # @berlinium @domain @ui @heiko
#  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
 # Scenario: Receive new NE from Plural for a non-existing Network Element Group

    #Given no NEG with name "NEG-123456" is existing in resource inventory
 #   Given create Mock
 #   And the plural mock will respond HTTP code 200 when called
 #   And open import-ui




  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
  Scenario: Receive new NE from Plural for a non-existing Network Element Group

    #Given delete neg in ri       erzeugt Fehler

    # And create Mock
    #When request to plural for NEG-name was sent
    # Beispiel: /pluralAlignment?nameNEG=49/30/111/POD/02
    #And response from plural for "NEG-123456" was received
    #When Import negname "NEG-123456"

    Given the plural mock will respond HTTP code 201 when called
    When trigger auto-import request to importer
    Then positive response from importer received
    #Then ri was created with neg and ne and nep
    Then Assert negname "egal"

    #Then 1 NEG update notification was sent to NEMO
  # NemoUpdaterRobot: checkLogicalResourcePutRequestToNemoWiremock

  # DB bereinigen
    And delete neg in ri


  # Queue löschen?









#  @berlinium @domain
 # @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
 # Scenario: create NEG in ri
    # neg will be deleted at the end of test
 #   Given a NEG with name "NEG-123456" is existing in A4 resource inventory