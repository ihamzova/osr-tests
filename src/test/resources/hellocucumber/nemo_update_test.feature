Feature: Nemo update
  Nemo has been triggered for update

  Background:
    Given no NEG with uuid "a123b" exists in A4 resource inventory

  Scenario: Nemo update for existent element
    Given a NEG with uuid "a123b" exists in A4 resource inventory
    When a Nemo update is triggered for uuid "a123b"
    Then Nemo should have gotten a Put request for uuid "a123b"

  Scenario: Nemo update for non-existent element
    When a Nemo update is triggered for uuid "a123b"
    Then Nemo should have gotten a Delete request for uuid "a123b"
