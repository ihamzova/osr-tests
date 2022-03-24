@DIGIHUB-93303
Feature: Upload CSV file in A4 ui which results in importing NEGs, NEs and NEPs into A4 resource inventory
  Some great description of what this feature is about.

  #@DIGIHUB-xxxxx
  Background:
    Given a user with Berlinium credentials

  @DIGIHUB-124555
  @team:berlinium @domain:osr @ui
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
  Scenario: Import a CSV file via ui
    Given no NEG with name "49/3333/1/POD/01" is existing in A4 resource inventory
    And no NEG with name "49/4444/1/POD/01" is existing in A4 resource inventory
    And no NE with VPSZ "49/1234/0" and FSZ "7KH3" is existing in A4 resource inventory
    And no NE with VPSZ "49/9876/1" and FSZ "7KE0" is existing in A4 resource inventory
    And the user has a CSV file with the following data:
      | NEG Name         | VPSZ      | FSZ  |
      | 49/3333/1/POD/01 | 49/2345/0 | 7KH3 |
      | 49/4444/1/POD/01 | 49/8765/1 | 7KE0 |
    When the user navigates to import page
    And uploads the CSV file
    Then a NEG with name "49/3333/1/POD/01" does exist in A4 resource inventory
    And a NEG with name "49/4444/1/POD/01" does exist in A4 resource inventory
    And a NE with VPSZ "49/2345/0" and FSZ "7KH3" does exist in A4 resource inventory
    And a NE with VPSZ "49/8765/1" and FSZ "7KE0" does exist in A4 resource inventory
    And 20 NEPs connected to the NE with VPSZ "49/2345/0" and FSZ "7KH3" do exist in A4 resource inventory
    And 56 NEPs connected to the NE with VPSZ "49/8765/1" and FSZ "7KE0" do exist in A4 resource inventory
    And 1 "PUT" update notification was sent to NEMO for the NEG with name "49/3333/1/POD/01"
    And 1 "PUT" update notification was sent to NEMO for the NEG with name "49/4444/1/POD/01"
    And 1 "PUT" update notification was sent to NEMO for the NE with VPSZ "49/2345/0" and FSZ "7KH3"
    And 1 "PUT" update notification was sent to NEMO for the NE with VPSZ "49/8765/1" and FSZ "7KE0"
    And 1 "PUT" update notification was sent to NEMO for each NEP connected to the NE with VPSZ "49/2345/0" and FSZ "7KH3"
    And 1 "PUT" update notification was sent to NEMO for each NEP connected to the NE with VPSZ "49/8765/1" and FSZ "7KE0"
