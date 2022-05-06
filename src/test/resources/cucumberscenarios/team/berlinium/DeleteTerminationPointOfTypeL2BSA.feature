@DIGIHUB-128941
Feature: Delete Termination Point of Type L2BSA_TP
  See https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=398395795 for details.

  # @DIGIHUB-127641
  # Scenario: NEMO deletes non-existent L2BSA Termination Point (idempotency test)
  # is fulfilled with Scenario: NEMO deletes non-existent Termination Point
  # in DpuCommissioningInA4PlatformDeleteFtthAccessLine

  #@DIGIHUB-xxxxxxx generate xray ticket here
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
  Scenario: NEMO deletes L2BSA Termination Point without NSP
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And no NSP L2BSA exists in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And the TP does not exist in A4 resource inventory anymore

  #@DIGIHUB-xxxxxxx generate xray ticket here
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
  Scenario: NEMO deletes L2BSA Termination Point with NSP connected
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And a NSP L2BSA is existing in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 400
    And the NSP L2BSA still exists in A4 resource inventory
    And the TP still exists in A4 resource inventory
