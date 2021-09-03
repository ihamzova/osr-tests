Feature: Nemo update
  Nemo has been triggered for update

  Background:
    Given test context is set up
    And Nemo wiremock is set up
    And test data is cleaned up for uuid "a123b"

  Scenario: Nemo update for existing element
    Given a NEG with uuid "a123b"
    When a Nemo update is triggered for uuid "a123b"
    Then Nemo should have gotten a Put request for uuid "a123b"

  Scenario: Nemo update for non-existing element
    Given no existing element with uuid "b123c"
    When a Nemo update is triggered for uuid "b123c"
    Then Nemo should have gotten a Delete request for uuid "a123b"
