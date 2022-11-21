# Partner Connect onboarding validation

## Overview
In order to ensure the best experience for our mutual customers, we require validation and sign-off for four different 
aspects of the integration.
1. Your team will first validate that the integration works end-to-end.
2. The Databricks engineering team will validate the Partner Connect integration functionally works in all use cases.
3. The Databricks partner solution architect team will validate the "core integration" works using Partner Connect's 
permissions setup.
4. The Databricks product team will validate the usability of the integration for a new user who is unfamiliar with 
your product.

## How you will validate your integration

### API testing
We require partners to validate their API implementation using the test suites provided in this project.

### Manual testing
We require partners to validate their end-to-end experience for all the applicable [test cases](OnboardingTestCases.md).  You may use either the Databricks workspace we provision or any other.  We can enable your tile in any workspace.

## How the Databricks engineering team will validate your integration
We will schedule a one-hour video call to manually execute the [test cases](OnboardingTestCases.md) with you.  Your team should come 
prepared with the ability to reset our test accounts and connections.

## How the Databricks partner solution architect team will validate your integration
A partner solution architect will validate the "core integration" works when used through Partner Connect.  This step does not require a meeting.

## How the Databricks product team will validate your integration
The Databricks product team will validate the usability of the integration from a new user's perspective.  In particular, the product team will validate:
1. A user does not have to select Databricks as the connector.
2. A user does not have to manually enter the configuration that was passed via the Connect API.
3. It is seamless for a user to find the Databricks connection when they land in the partner product.  The user will not get lost trying to find or use the connection to Databricks.
