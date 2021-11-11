#Feature: Nemo update
#  Nemo has been triggered for update
#
#  Background:
#    Given no NEG with uuid "e24bab91-4d2e-4f23-8510-b86528711e7d" exists in A4 resource inventory
#    And NEMO wiremock is set up
#
#  Scenario: Nemo update for existent element
#    Given a NEG with uuid "e24bab91-4d2e-4f23-8510-b86528711e7d" exists in A4 resource inventory
#    When a Nemo update is triggered for uuid "e24bab91-4d2e-4f23-8510-b86528711e7d"
#    Then Nemo should have gotten a Put request for uuid "e24bab91-4d2e-4f23-8510-b86528711e7d"
#
#  Scenario: Nemo update for non-existent element
#    When a Nemo update is triggered for uuid "e24bab91-4d2e-4f23-8510-b86528711e7d"
#    Then Nemo should have gotten a Delete request for uuid "e24bab91-4d2e-4f23-8510-b86528711e7d"
