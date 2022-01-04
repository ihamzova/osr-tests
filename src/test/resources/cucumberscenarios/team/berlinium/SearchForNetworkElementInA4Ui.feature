Feature: [DIGIHUB-xxxxx][Berlinium] Network Element search in A4 res-inv UI

  Background:
    Given user "autotest.berlinium" with password "autotest.03-Berlinium" is logged in to "a4-resource-inventory-ui"

  @berlinium
  @a4-resource-inventory-ui @a4-resource-inventory-bff-proxy @a4-resource-inventory
  Scenario Outline: User wants to look up an existing Network Element
    Given a NE with VPSZ <VPSZ> and FSZ <FSZ> is existing in A4 resource inventory
    And a NE with VPSZ <VPSZ> and FSZ "7KH1" is existing in A4 resource inventory
    When the user navigates to NE search page
    And enters VPSZ <VPSZ> into the input fields
    And enters FSZ <FSZ> into the input field
    And clicks the submit button
    Then 1 NE is shown in the search result list
    And 1 NE in the search result list has VPSZ <VPSZ> and FSZ <FSZ>

    Examples:
      | VPSZ        | FSZ    |
      | "49/1234/0" | "7KH0" |

  @berlinium
  @a4-resource-inventory-ui @a4-resource-inventory-bff-proxy @a4-resource-inventory
  Scenario Outline: User wants to look up an existing Network Element; multiple results
    Given a NE with VPSZ <VPSZ> and FSZ "7KH0" is existing in A4 resource inventory
    And a NE with VPSZ <VPSZ> and FSZ "7KH1" is existing in A4 resource inventory
    When the user navigates to NE search page
    And enters VPSZ <VPSZ> into the input fields
    And clicks the submit button
    Then 2 NEs are shown in the search result list
    And 2 NEs in the search result list have VPSZ <VPSZ>

    Examples:
      | VPSZ        |
      | "49/1234/0" |

  @berlinium
  @a4-resource-inventory-ui @a4-resource-inventory-bff-proxy @a4-resource-inventory
  Scenario: User wants to look up a non-existing Network Element
    Given no NE exists in A4 resource inventory
    When the user navigates to NE search page
    And enters VPSZ "49/1234/0" into the input fields
    And clicks the submit button
    Then the NE search result list is empty
