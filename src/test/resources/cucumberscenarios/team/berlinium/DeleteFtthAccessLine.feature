@REG_DIGIHUB-118272
Feature: Berlinium parts of DPU Commissioning in A4 platform - Delete FTTH Access line

# US DIGIHUB-118969, Scenario #1
# X-Ray: DIGIHUB-127641
  @berlinium @domain
  @a4-resource-inventory @a4-resource-inventory-service
  Scenario: NEMO deletes non-existent TP (idempotency test)
    Given no TP exists in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202

# DIGIHUB-118969, Scenario #2
# NOTE: was replaced by DIGIHUB-121769, scenario #2
# X-Ray: DIGIHUB-127643
  @berlinium @domain
  @a4-resource-inventory @a4-resource-inventory-service
  Scenario: NEMO deletes TP with valid type PON
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202

# DIGIHUB-118969, Scenario #3, #4, and #5
# X-Ray: DIGIHUB-127642
  @berlinium @domain
    @a4-resource-inventory-service
  Scenario Outline: NEMO deletes TP with invalid types
    Given a TP with type "<Type>" is existing in A4 resource inventory
    When NEMO sends a delete TP request
    Then the request is responded with HTTP error code 400

    Examples:
      | Type      |
      | G_FAST_TP |
      | G.FAST_TP |
      | A10NSP_TP |
      | L2BSA_TP  |
    # NOTE: What is the currently official type name? G_FAST_TP or G.FAST_TP?

#--------------------------------------

# DIGIHUB-121769, scenario #1
  # X-Ray: DIGIHUB-127854
# NOTE: Will be replaced by DIGIHUB-118971, scenario #1
  @berlinium
  @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-commissioning
  Scenario: NEMO deletes TP with NSP attached, therefore deprovisioning to U-Piter is triggered
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And U-Piter DPU wiremock will respond HTTP code 202 when called, and do a callback
    When NEMO sends a delete TP request

    # use (and implement!!) this step when using u-piter wiremock. Then the wiremock webhook can do the NSP deletion, and the callback is the below manual step
#    And U-Piter sends the callack
    Then the request is responded with HTTP code 202
    And a DPU deprovisioning request to U-Piter was triggered with Line ID "DEU.DTAG.12345"

    # Following line won't work: U-Piter DPU deprovisioning wiremock cannot perform deletion of NSP, therefore TP cannot be deleted. Possible to test with full domain test.
#    And the TP does not exist in A4 resource inventory anymore

# DIGIHUB-121769, scenario #2
  # X-Ray: DIGIHUB-127643 (existent scenario there will be overwritten by this one)
# NOTE: This is the same as DIGIHUB-118969, Scenario #2, with added Then steps (TP deleted and no deProv call)
  @berlinium
  @a4-resource-inventory @a4-resource-inventory-service @a4-queue-dispatcher @a4-commissioning
  Scenario: NEMO deletes TP without attached NSP, therefore deprovisioning to U-Piter is not triggered
    Given a TP with type "PON_TP" is existing in A4 resource inventory

    # Following line is redundant: No NSP created anyway.
#    And no NSP FTTH exists in A4 resource inventory for the TP

    And U-Piter DPU wiremock will respond HTTP code 202 when called, and do a callback
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And no DPU deprovisioning request to U-Piter was triggered
    And the TP does not exist in A4 resource inventory anymore

# DIGIHUB-121769, scenario #3
# Will not be automatised: Scenarios are not supposed to look into "inner workings" like queue contents.

#--------------------------------------

## DIGIHUB-121769, scenario #4, and #5
  Scenario Outline: trigger deprovisioning - retry
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And U-Piter DPU wiremock will respond HTTP code <HTTPCode> when called
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    # the uuid is now in our queue and is going to be retried after 2.5 min, that is checked in next steps
    Given clean wiremock
    Given U-Piter DPU wiremock will respond HTTP code 202 when called
    Then wait 3min
    And a DPU deprovisioning request to U-Piter was triggered with Line ID "DEU.DTAG.12345"

    Examples:
      | HTTPCode |
      | 408      |
      | 500      |

## DIGIHUB-121769, scenario #6
  Scenario Outline: trigger deprovisioning - give up
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And DLQ is empty
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    And U-Piter DPU wiremock will respond HTTP code <HTTPCode> when called
    When NEMO sends a delete TP request
    Then the request is responded with HTTP code 202
    And a DPU deprovisioning request to U-Piter was triggered with Line ID "DEU.DTAG.12345"
#    And a log entry with message "U-Piter not available" has been written
    And TP UUID is added to DLQ

    Examples:
      | HTTPCode |
      | 400      |
      | 401      |
      #| 403      | this will be retried, because it can be because of token expiration

#--------------------------------------

## DIGIHUB-118971, scenario #1
## NOTE: Originally this scenario is _without_ NSP, but this is redundant to DIGIHUB-121769, scenario #2, therefore we change this one to include NSP
## NOTE: This is the same as DIGIHUB-121769, scenario #1, with added Then step (NSP not in repo anymore)
  Scenario: Receive delete TP "Sunny Day" - NSP is existing for TP
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH-Access is not existing in A4 resource inventory for the TP
    When U-Piter sends the callback
    Then the request is responded with HTTP code 200
    Then the TP does not exist in A4 resource inventory anymore

#
#    # TODO: Scenario for when U-Piter callback contains error msg
#
## DIGIHUB-118971, scenario #2
## No scenario for this: DIGIHUB-118971, scenario #1 takes care of good day
## Instead we create a small scenario with given TP and NSP connected to TP, then a4-res-inv DELETE request for TP, which is rejected from RI because of constraint violation
## current uuid is put into DLQ
  Scenario: Delete TP with attached NSP – Error provocation
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And a NSP FTTH with Line ID "DEU.DTAG.12345" is existing in A4 resource inventory for the TP
    When U-Piter sends the callback
    Then the request is responded with HTTP code 200
#    And a log entry with message "Constraint Violation: NSP connected to TP" has been written
#
## DIGIHUB-118971, scenario #3
## NOTE: Redundant to DIGIHUB-118969, Scenario #1. Skipped.
#
## DIGIHUB-118971, scenario #4
  # Scenario Outline: Delete TP - RI is down
#    Given a TP with type "PON_TP" is existing in A4 resource inventory
#    And A4 resource inventory will respond HTTP code "<HTTPCode>" when called
#    When NEMO sends a delete TP request
#    Then the request is responded with HTTP code 202
#    And a log entry with message "A4 Resource Inventory not available" has been written

#    Examples:
#      | HTTPCode |
#      | 500      |
#      | 503      |

  ## DIGIHUB-118971, scenario #4
   Scenario Outline: Delete TP - RI is down
    Given a TP with type "PON_TP" is existing in A4 resource inventory
    And A4 resource inventory will respond HTTP code <HTTPCode> when called
    When U-Piter sends the callback
    Then the request is responded with HTTP code 200


    Examples:
      | HTTPCode |
      | 500      |