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

#  @team:berlinium @domain:osr @ui
#  @ms:a4-resource-inventory @ms:a4-nemo-updater @ms:a4-inventory-importer
#  Scenario: Import a NEG, some NEGs and NEPs from Plural via UI
#    Given the Plural mock will respond HTTP code 201 and provide the following NE data when called with NEG name "bla":
#      | VPSZ | FSZ |
#      | 1    | 2   |
#      | 3    | 4   |
#    When the user navigates to import page
#    And enters NEG name "bla" into the input field
#    And clicks the import submit button
#    Then the ui displays a positive response
#    And a NEG with name "bla" does exist in A4 resource inventory
#    And a NE with VPSZ "1" and FSZ "2" does exist in A4 resource inventory
#    And a NE with VPSZ "3" and FSZ "4" does exist in A4 resource inventory
#    And 23 NEPs connected to the NE with VPSZ "1" and FSZ "2" do exist in A4 resource inventory
#    And 17 NEPs connected to the NE with VPSZ "3" and FSZ "4" do exist in A4 resource inventory
#    And 1 "PUT" update notification was sent to NEMO for the NEG with name "bla"
#    And 1 "PUT" update notification was sent to NEMO for the NE with VPSZ "1" and FSZ "2"
#    And 1 "PUT" update notification was sent to NEMO for the NE with VPSZ "3" and FSZ "4"
#    And 1 "PUT" update notification was sent to NEMO for each of the 23 NEPs connected to the NE with VPSZ "1" and ENDSZ "2"
#    And 1 "PUT" update notification was sent to NEMO for each of the 17 NEPs connected to the NE with VPSZ "3" and ENDSZ "4"
