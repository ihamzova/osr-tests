#Feature: Network Element search in A4 res-inv UI
#
#  Background:
#    Given a NE with VSPZ "49/1234/0" and FSZ "7KH0" is existing in A4 resource inventory
#    And user "autotest.berlinium" is logged in to a4-resource-inventory-ui with password "autotest.03-Berlinium"
#
#    # !!! THIS SCENARIO FAILS! This is just a "dummy" scenario I'm using to analyse the UI problems when using BDD/Cucumber
#    @berlinium
#    @a4-resource-inventory-ui @a4-resource-inventory-bff-proxy @a4-resource-inventory
#    Scenario: User wants to look up an existing NE
#      When the user opens NE search page
#      And enters VPSZ "49/1234/0" and FSZ "7KH0" into the input fields
#      And clicks the submit button
#      Then the wanted NE is shown in the search result table

  # DEACTIVATED; Belas playground to get UI BDD tests to work
