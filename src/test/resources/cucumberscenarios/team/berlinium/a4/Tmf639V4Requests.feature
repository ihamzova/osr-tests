##@DIGIHUB-76376
#Feature: CA-Integration sends GET requests to A4 resource inventory via the TMF639 v4 API
#  Some great description of what this feature is about.
#
#  #@DIGIHUB-xxxxxx
#  @team:berlinium @domain:osr
#  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
#  Scenario: CA-Integration sends GET request for an existing Network Service Profile A10NSP
#    Given a NSP A10NSP with itAccountingKey "bla" and NetworkElementLinkUuid "blubb" is existing in A4 resource inventory
#    When CA-Integration sends a GET request for the NSP A10NSP via the TMF639 v4 API
#    Then the request is responded with HTTP code 200
#
#  #@DIGIHUB-xxxxxx
#  @team:berlinium @domain:osr
#  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
#  Scenario: CA-Integration sends GET request for an non-existing Network Service Profile A10NSP
#    Given no NSP A10NSP exists in A4 resource inventory
#    When CA-Integration sends a GET request for the NSP A10NSP via the TMF639 v4 API
#    Then the request is responded with HTTP code 404
#
#  #@DIGIHUB-xxxxxx
#  @team:berlinium @domain:osr
#  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
#  Scenario: CA-Integration sends GET request for existing Network Service Profiles A10NSP with query parameter itAccountingKey
#    Given a NSP A10NSP with itAccountingKey "bla" and NetworkElementLinkUuid "blubb" is existing in A4 resource inventory
#    When CA-Integration sends a GET request for NSPs A10NSP with query param itAccountingKey = "bla" via the TMF639 v4 API
#    Then the request is responded with HTTP code 200
#    And the response contains 1 NSP A10NSP in TMF639 v4 format
#
#  #@DIGIHUB-xxxxxx
#  @team:berlinium @domain:osr
#  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
#  Scenario: CA-Integration sends GET request for non-existing Network Service Profiles A10NSP with query parameter itAccountingKey
#    Given a NSP A10NSP with itAccountingKey "bla" and NetworkElementLinkUuid "blubb" is existing in A4 resource inventory
#    When CA-Integration sends a GET request for NSPs A10NSP with query param itAccountingKey = "no-bla" via the TMF639 v4 API
#    Then the request is responded with HTTP code 200
#    And the response contains 0 NSP A10NSP in TMF639 v4 format
#
#  #@DIGIHUB-xxxxxx
#  @team:berlinium @domain:osr
#  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
#  Scenario: CA-Integration sends GET request for existing Network Service Profiles A10NSP with query parameter NetworkElementLinkUuid
#    Given a NSP A10NSP with itAccountingKey "bla" and NetworkElementLinkUuid "blubb" is existing in A4 resource inventory
#    When CA-Integration sends a GET request for NSPs A10NSP with query param NetworkElementLinkUuid = "blubb" via the TMF639 v4 API
#    Then the request is responded with HTTP code 200
#    And the response contains 1 NSP A10NSP in TMF639 v4 format
#
#  #@DIGIHUB-xxxxxx
#  @team:berlinium @domain:osr
#  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
#  Scenario: CA-Integration sends GET request for non-existing Network Service Profiles A10NSP with query parameter NetworkElementLinkUuid
#    Given a NSP A10NSP with itAccountingKey "bla" and NetworkElementLinkUuid "blubb" is existing in A4 resource inventory
#    When CA-Integration sends a GET request for NSPs A10NSP with query param NetworkElementLinkUuid = "no-blubb" via the TMF639 v4 API
#    Then the request is responded with HTTP code 200
#    And the response contains 0 NSP A10NSP in TMF639 v4 format
#
#  #@DIGIHUB-xxxxxx
##  @team:berlinium @domain:osr
##  @ms:a4-resource-inventory @ms:a4-resource-inventory-service
##  Scenario: CA-Integration sends GET request for Network Service Profiles A10NSP with both query parameters
##    Given a NSP A10NSP with itAccountingKey "bla" and NetworkElementLinkUuid "blubb" is existing in A4 resource inventory
##    When CA-Integration sends a GET request for NSPs A10NSP with query params itAccountingKey = "bla" and NetworkElementLinkUuid = "blubb" via the TMF639 v4 API
##    Then the request is responded with HTTP code 200
##    And the response contains 1 NSP A10NSP in TMF639 v4 format
