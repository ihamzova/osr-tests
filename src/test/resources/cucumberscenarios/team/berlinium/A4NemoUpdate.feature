@REG_DIGIHUB-128553
Feature: [DIGIHUB-128553][Berlinium] Sending Update Calls to Nemo
  #

  # X-Ray: DIGIHUB-xxxxxx Trigger an update call (PUT) to NEMO for existing network element group
  @berlinium
  @a4-resource-inventory @a4-resource-inventory-service @a4-nemo-updater
  Scenario: Trigger an update call to NEMO for existing NEG
    Given a NEG is existing in A4 resource inventory
    When Trigger an Update Call to Nemo for NetworkElementGroup
    Then 1 "PUT" NEG update notifications was sent to NEMO

  # X-Ray: DIGIHUB-xxxxxx Trigger an update call (DELETE) to NEMO for non-existing entity type element
  @berlinium
  @a4-resource-inventory @a4-resource-inventory-service @a4-nemo-updater
  Scenario: Trigger an update call to NEMO for non-existing NEG
    Given no NEG exists in A4 resource inventory
    When Trigger an Update Call to Nemo for NetworkElementGroup
    Then 1 "DELETE" NEG update notifications was sent to NEMO


