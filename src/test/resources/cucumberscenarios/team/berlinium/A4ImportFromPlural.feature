@DIGIHUB-117772
Feature: Send "Knoten" request to PLURAL and import the returning data into A4 resource inventory
  See https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=395584564 for details.

  #@DIGIHUB-144188
  Background:
    Given a user with Berlinium credentials

  @DIGIHUB-144187
  @team:berlinium @domain:osr @ui @test
  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
  Scenario: Import a NEG, some NEGs and NEPs from Plural via UI
    Given no NEG with name "49/1111/1/POD/01" is existing in A4 resource inventory
    And no NE with VPSZ "49/1234/0" and FSZ "7KH3" is existing in A4 resource inventory
    And no NE with VPSZ "49/9876/1" and FSZ "7KE0" is existing in A4 resource inventory
    And the Plural mock will respond HTTP code 201 and provide the following NE data when called with NEG name "49/1111/1/POD/01":
      | VPSZ      | FSZ  |
      | 49/1234/0 | 7KH3 |
      | 49/9876/1 | 7KE0 |
    When the user navigates to import page
    And enters NEG name "49/1111/1/POD/01" into the input field
    And clicks the import submit button
    Then the ui displays a positive response
    And a NEG with name "49/1111/1/POD/01" does exist in A4 resource inventory
    And a NE with VPSZ "49/1234/0" and FSZ "7KH3" does exist in A4 resource inventory
    And a NE with VPSZ "49/9876/1" and FSZ "7KE0" does exist in A4 resource inventory
    And 20 NEPs connected to the NE with VPSZ "49/1234/0" and FSZ "7KH3" do exist in A4 resource inventory
    And 56 NEPs connected to the NE with VPSZ "49/9876/1" and FSZ "7KE0" do exist in A4 resource inventory
    And 1 "PUT" update notification was sent to NEMO for the NEG with name "49/1111/1/POD/01"
    And 1 "PUT" update notification was sent to NEMO for the NE with VPSZ "49/1234/0" and FSZ "7KH3"
    And 1 "PUT" update notification was sent to NEMO for the NE with VPSZ "49/9876/1" and FSZ "7KE0"
    And 1 "PUT" update notification was sent to NEMO for each NEP connected to the NE with VPSZ "49/1234/0" and FSZ "7KH3"
    And 1 "PUT" update notification was sent to NEMO for each NEP connected to the NE with VPSZ "49/9876/1" and FSZ "7KE0"
