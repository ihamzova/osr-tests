Feature: Network Element search in A4 res-inv UI

  Background:
    Given user "autotest.berlinium" with password "autotest.03-Berlinium" is logged in to "a4-resource-inventory-ui"

  @berlinium
  @a4-resource-inventory-ui @a4-resource-inventory-bff-proxy @a4-resource-inventory
  Scenario Outline: User wants to look up an existing NE (INCOMPLETE!)
    Given a NE with VSPZ <VPSZ> and FSZ <FSZ> is existing in A4 resource inventory
    When the user opens NE search page
    And enters VPSZ <VPSZ> and FSZ <FSZ> into the input fields
    And clicks the submit button
    #Then the wanted NE is shown in the search result table

    Examples:
      | VPSZ        | FSZ    |
      | "49/1234/0" | "7KH0" |
