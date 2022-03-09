Feature: [DIGIHUB-xxxxx][Berlinium] Receiving Inventory from Plural


  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
  Scenario: Receive new NE from Plural for a non-existing Network Element Group

    Given no NEG with name "NEG-123456" is existing in resource inventory
    #When request to plural for NEG-name was sent
    And Mock negname "NEG-123456"


    # UI Endpoint als Aufruf integrieren "/pluralAlignment"
    # Beispiel: /pluralAlignment?nameNEG=49/30/111/POD/02
    #And response from plural for "NEG-123456" was received
    #When Import negname "NEG-123456"


    And the plural mock will respond HTTP code 200 when called
    #And the program say hello
    #Then ri was updated with neg and ne
    #Then 1 NEG update notification was sent to NEMO
    Then Assert negname "NEG-123456"


#  @berlinium @domain
 # @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
 # Scenario: create NEG in ri
    # neg will be deleted at the end of test
 #   Given a NEG with name "NEG-123456" is existing in A4 resource inventory