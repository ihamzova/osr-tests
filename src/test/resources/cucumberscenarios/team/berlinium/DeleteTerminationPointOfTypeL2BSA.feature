@DIGIHUB-128941
Feature: Delete Termination Point of Type L2BSA_TP
  See https://gard.telekom.de/gardwiki/pages/viewpage.action?pageId=398395795 for details.

 #@DIGIHUB-xxxxxx
  @team:berlinium @domain:osr
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
  Scenario: NEMO deletes non-existent L2BSA Termination Point (idempotency test)
    Given no TP exists in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202

  #@DIGIHUB-xxxxxxx
  @team:berlinium @smoke
  @ms:a4-resource-inventory @ms:a4-resource-inventory-service @ms:a4-queue-dispatcher @ms:a4-commissioning
  Scenario: NEMO deletes L2BSA Termination Point without NSP
    Given a TP with type "L2BSA_TP" is existing in A4 resource inventory
    And no NSP L2BSA exists in A4 resource inventory for the TP
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And the TP does not exist in A4 resource inventory anymore


  #@DIGIHUB-127642 ACHTUNG ACHTUNG ACHTUNG!!!!!!!!!!!!! L2BSA_TP ist nun ein gültiger Typ
    @team:berlinium @domain:osr
    @ms:a4-resource-inventory-service
  Scenario Outline: NEMO deletes Termination Point with invalid types
    Given a TP with type "<Type>" is existing in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP error code 400

    Examples:
      | Type      |
      | G_FAST_TP |
      | G.FAST_TP |
      | A10NSP_TP |
      | L2BSA_TP  |

