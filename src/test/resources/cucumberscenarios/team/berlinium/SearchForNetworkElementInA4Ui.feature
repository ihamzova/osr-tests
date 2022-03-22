@DIGIHUB-90603
Feature: Network Element search in A4 res-inv UI
  Some great description of what this feature is about.

  #@DIGIHUB-xxxxx
  Background:
    Given a user with Berlinium credentials

  @team:berlinium @domain:osr @smoke @ui
    @ms:a4-resource-inventory-ui @ms:a4-resource-inventory-bff-proxy @ms:a4-resource-inventory
  Scenario Outline: User wants to look up an existing Network Element
    Given a NE with VPSZ <VPSZ> and FSZ <FSZ> is existing in A4 resource inventory
    And a NE with VPSZ <VPSZ> and FSZ "7KH1" is existing in A4 resource inventory
    When the user navigates to NE search page
    And enters VPSZ <VPSZ> into the input fields
    And enters FSZ <FSZ> into the input field
    And clicks the NE search submit button
    Then 1 NE is shown in the search result list
    And 1 NE in the search result list has VPSZ <VPSZ> and FSZ <FSZ>

    Examples:
      | VPSZ        | FSZ    |
      | "49/1234/0" | "7KH0" |

  @team:berlinium @domain:osr @ui
    @ms:a4-resource-inventory-ui @ms:a4-resource-inventory-bff-proxy @ms:a4-resource-inventory
  Scenario Outline: User wants to look up an existing Network Element; multiple results
    Given a NE with VPSZ <VPSZ> and FSZ "7KH0" is existing in A4 resource inventory
    And a NE with VPSZ <VPSZ> and FSZ "7KH1" is existing in A4 resource inventory
    When the user navigates to NE search page
    And enters VPSZ <VPSZ> into the input fields
    And clicks the NE search submit button
    Then 2 NEs are shown in the search result list
    And 2 NEs in the search result list have VPSZ <VPSZ>

    Examples:
      | VPSZ        |
      | "49/1234/0" |

  @team:berlinium @domain:osr @ui
  @ms:a4-resource-inventory-ui @ms:a4-resource-inventory-bff-proxy @ms:a4-resource-inventory
  Scenario: User wants to look up a non-existing Network Element
    Given no NE exists in A4 resource inventory
    When the user navigates to NE search page
    And enters VPSZ "49/1234/0" into the input fields
    And clicks the NE search submit button
    Then the NE search result list is empty
