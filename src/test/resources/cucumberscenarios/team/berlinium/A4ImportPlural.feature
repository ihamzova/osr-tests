Feature: [DIGIHUB-142105][Berlinium] Receiving Inventory from Plural


  @berlinium @domain
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
  Scenario: Receive new NE from Plural for a non-existing Network Element Group

    Given the plural mock will respond HTTP code 201 when called
    And delete neg in ri recursively
    When trigger auto-import request to importer
    # positive response    thenTheRequestIsRespondedWithHTTPCode gibt es in common...
    Then positive response from importer received
    And ri was created with neg and ne and neps
    # Bela: Funktion aus NemoRobot hier verwenden: checkAsyncNemoUpdatePutRequests
    And update notifications was sent to NEMO
   # And delete neg in ri recursively


