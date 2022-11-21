# Test Cases

- [Creating a new connection](OnboardingTestCases.md#creating-a-new-connection)
  - [P100](OnboardingTestCases.md#P100)
  - [P101](OnboardingTestCases.md#P101)
- [Core integration](OnboardingTestCases.md#core-integration)
  - [P200](OnboardingTestCases.md#P200)
  - [P201](OnboardingTestCases.md#P201)
- [Reusing an existing connection](OnboardingTestCases.md#reusing-an-existing-connection)
  - [P300](OnboardingTestCases.md#P300)
  - [P301](OnboardingTestCases.md#P301)
  - [P302](OnboardingTestCases.md#P302)
- [Deleting and re-creating a connection](OnboardingTestCases.md#deleting-and-re-creating-a-connection)
  - [P400](OnboardingTestCases.md#P400)
  - [P401](OnboardingTestCases.md#P401)
  - [P402](OnboardingTestCases.md#P402)
- [Deleting and re-creating an account](OnboardingTestCases.md#deleting-and-re-creating-an-account)
  - [P403](OnboardingTestCases.md#P403)
  - [P404](OnboardingTestCases.md#P404)
  - [P405](OnboardingTestCases.md#P405)
- [Expired trial scenarios](OnboardingTestCases.md#expired-trial-scenarios)
  - [P500](OnboardingTestCases.md#P500)
  - [P501](OnboardingTestCases.md#P501)
  - [P502](OnboardingTestCases.md#P502)
- [Multiple workspace scenario](OnboardingTestCases.md#multiple-workspace-scenario)
  - [P600](OnboardingTestCases.md#P600)
- [Demo flag scenario](OnboardingTestCases.md#demo-flag-scenario)
  - [P700](OnboardingTestCases.md#P700)
- [Test-Connection test-hook](OnboardingTestCases.md#test-connection-test-hook)
  - [P800](OnboardingTestCases.md#P800)
  - [P801](OnboardingTestCases.md#P801)
- [Connector API](OnboardingTestCases.md#connector-api-for-ingestion-partners)
  - [P900](OnboardingTestCases.md#P900)

# Overview
Below is enumerated all the Partner Connect test cases.  The format is:

## Test-Case-Identifier
|Topic |Description |
|---|----|
|Explanation |A description of the user-journey |
|When this test case applies |Which partners this test case is applicable to.  Some test cases will be inapplicable to most partners. |
|Is the API test automated? |Does this repo have an automated test for this test case.  Note that this testing is limited to your API's response.  It does not include any behavior after the redirect_url is returned. |
|Expectations when testing manually |During final validation of your Partner Connect integration, these expectations must be met for Databricks to sign-off on the integration. |

## Glossary
1. "New user" - A user email address that the partner **has not** seen before.
2. "Existing user" - A user email address that the partner **has** seen before.
3. "New workspace" - A workspace_id that the partner **has not** seen before.
4. "Existing workspace" - A workspace_id that the partner **has** seen before.
5. "Account" - The top organization level concept for a customer.  E.g. Adobe is an Account.
6. "Core integration" - The prerequisite integration between the partner product and Databricks.  E.g. a BI tool being able to query a manually configured Databricks SQL Warehouse.

# Creating a new connection

## P100
|Topic |Description |
|---|----|
|Explanation |A new user in a new workspace creates a connection |
|When this test case applies |For any partners that implement the Connect API |
|Is the API test automated? |Yes, in PartnerConnectTest.scala |
|Expectations when testing manually |This flow **must** be seamless.  This is our primary Critical User Journey.<br /><br />The user must never be uncertain how to proceed to see the Databricks connection working.<br />The Databricks connector in the partner product **must** be pre-populated.<br />The user **must** not need to select Databricks as a destination.   |

## P101
|Topic |Description |
|---|----|
|Explanation |An existing user in a new workspace creates connection.  i.e. The partner already has user@account.com in their system before the Connect API is called with user@account.com.|
|When this test case applies |For any partners that implement the Connect API |
|Is the API test automated? |No |
|Expectations when testing manually |This flow **must** be seamless.<br /><br />The user must never be uncertain how to proceed to see the Databricks connection working.<br />The Databricks connector in the partner product **must** be pre-populated.<br />The user **must** not need to select Databricks as a destination. |


# Core integration
While the other test cases focus on correctly handling users, workspaces, and connections, these test cases verify connectivity from your product to Databricks as part of the Partner Connect integration.  The specific scenario depends on the partner's "core integration" but some examples include:
- Verifying the partner product can query data from Databricks
- Verifying the partner product can write data to Databricks
- Verifying the partner product can publish a notebook to Databricks and the notebook runs without errors.

## P200
|Topic |Description |
|---|----|
|Explanation |Your "core integration" works with the Partner Connect integration. |
|When this test case applies |All partners |
|Is the API test automated? |No |
|Expectations when testing manually |The "core integration" must work.  We must see connectivity between the two products.  If the integration uses Databricks data, test with both Unity Catalog and Hive Metastore (even if a partner doesn't support Unity Catalog yet, a Unity Catalog catalog can be used if it's the default catalog for the workspace). |

## P201
|Topic |Description |
|---|----|
|Explanation |Destination Location (for hive_metastore) and External Location (for Unity Catalog) support.<br />  For partners that use these fields to create External Tables in Databricks, this test confirms that the selection from the user in Databricks is respected by the partner.|
|When this test case applies |If the partner is configured for Destination/External Location support.  Typically this is just Ingestion partners. |
|Is the API test automated? |No |
|Expectations when testing manually |1. When a user doesn't select a Destination/External Location, the table created is Managed.<br />2. When a user does select a Destination/External Location, the table created is External with the cloud path. |

# Reusing an existing connection
Once a Partner Connect connection is established, all users in the Databricks workspace will be able to reuse the established connection.  These tests cases cover the different reuse cases.
## P300
|Topic |Description |
|---|----|
|Explanation |The user that created the connection, reuses the connection. |
|When this test case applies |For any partners that implement the Connect API |
|Is the API test automated? |Yes, in PartnerConnectTest.scala |
|Expectations when testing manually |The user must be able to easily find their Databricks connector. |

## P301
|Topic |Description |
|---|----|
|Explanation |A different **existing** user reuses the connection. |
|When this test case applies |For any partners that implement the Connect API |
|Is the API test automated? |No |
|Expectations when testing manually |The user must be able to easily find their Databricks connector. |

## P302
|Topic |Description |
|---|----|
|Explanation |A different **new** user reuses the connection. |
|When this test case applies |For any partners that implement the Connect API |
|Is the API test automated? |Yes, in NewUserTests.scala |
|Expectations when testing manually |The user must be able to easily find their Databricks connector. |

# Deleting and re-creating a connection
If a Partner Connect connection gets deleted (either within the Databricks product or the partner product), these tests cases validate correct behavior when a user then comes to Partner Connect.

## P400
|Topic |Description |
|---|----|
|Explanation |Databricks user creates a connection, deletes the connection in Databricks, then creates a connection. |
|When this test case applies |For any partners that implement the Connect API |
|Is the API test automated? |Yes, in DeleteConnectionTests.scala |
|Expectations when testing manually |Partner must create a new Databricks connection.  They may optionally delete the previous connection associated with the workspace. |

## P401
|Topic |Description |
|---|----|
|Explanation |Databricks user creates a connection, deletes the connection in Databricks, deletes the connection in the partner product, then creates a connection. |
|When this test case applies |For any partners that implement the Connect API that support deleting connections in their product. |
|Is the API test automated? |Yes, in DeleteConnectionTests.scala |
|Expectations when testing manually |Partner must create a new Databricks connection. |

## P402
|Topic |Description |
|---|----|
|Explanation |Databricks user creates a connection, deletes the connection in the partner product, then creates a connection. |
|When this test case applies |For any partners that implement the Connect API that support deleting connections in their product. |
|Is the API test automated? |Yes, in DeleteConnectionTests.scala |
|Expectations when testing manually |The partner should return 404 CONNECTION_NOT_FOUND resulting in an informative error in Databricks |

# Deleting and re-creating an account
In order to have repeatable testing, we require a Delete Account test hook.  The following tests confirm the correct behavior when that test hook is invoked.  These tests are often optional for validating the Partner Connect integration as they are often test-only.

## P403
|Topic |Description |
|---|----|
|Explanation |Databricks user creates a connection, deletes the connection in Databricks, deletes the account in the partner product, then creates a connection. |
|When this test case applies |For any partners that implement the Delete Account test-hook API |
|Is the API test automated? |Yes, in DeleteAccountTests.scala |
|Expectations when testing manually |None |

## P404
|Topic |Description |
|---|----|
|Explanation |Databricks user creates a connection, deletes the connection in the partner product, deletes the account in the product, then reuses the connection.  This should result in CONNECTION_NOT_FOUND. |
|When this test case applies |For any partners that implement the Delete Account test-hook API |
|Is the API test automated? |Yes, in DeleteAccountTests.scala |
|Expectations when testing manually |None |

## P405
|Topic |Description |
|---|----|
|Explanation |Calling Delete Account test-hook API should fail for non-demo, non-test email domains.  We're ensuring your test-hook cannot be used for deleting real customer data. |
|When this test case applies |For any partners that implement the Delete Account test-hook API |
|Is the API test automated? |Yes, in DeleteAccountTests.scala |
|Expectations when testing manually |None |

# Expired trial scenarios
Many partners have time-based free trials (e.g. 14 days or 30 days).  These tests validate the behavior when Partner Connect is used and routes to a partner's expired trial.

## P500
|Topic |Description |
|---|----|
|Explanation |Create a connection, expire the trial, delete the connection in Partner Connect, create a connection.<br />Alternatively the user may have already had a trial with the partner product outside of Databricks Partner Connect that is expired and attempts to be reused. |
|When this test case applies |For any partners that implement the Connect API and have time-based trials. |
|Is the API test automated? |No |
|Expectations when testing manually |Optional given the low-priority.  The experience should have a meaningful error. |

## P501
|Topic |Description |
|---|----|
|Explanation |Create a connection, expire the trial, reuse the connection. |
|When this test case applies |For any partners that implement the Connect API and have time-based trials. |
|Is the API test automated? |Yes, in ExpiredAccountTests.scala |
|Expectations when testing manually |Optional given the low-priority.  The experience should have a meaningful error. |

## P502
|Topic |Description |
|---|----|
|Explanation |Create a connection, expire the trial, reuse the connection as "new user". |
|When this test case applies |For any partners that implement the Connect API and have time-based trials. |
|Is the API test automated? |Yes, in ExpiredAccountTests.scala |
|Expectations when testing manually |Optional given the low-priority.  The experience should have a meaningful error. |

# Multiple workspace scenario
In Databricks, a user can belong to multiple Databricks workspaces.  It is a valid scenario for a user to click the partner's Partner Connect tile from multiple workspaces.
## P600
|Topic |Description |
|---|----|
|Explanation |A user creates a connection from Databricks workspace1, then creates a connection from Databricks workspace2. |
|When this test case applies |For any partners that implement the Connect API |
|Is the API test automated? |Yes, in MultipleWorkspaceTests.scala |
|Expectations when testing manually |The user must never be uncertain how to proceed to see the second Databricks connection working.<br />The second Databricks connector in the partner product **must** be pre-populated.<br />The user **must** not need to select Databricks as a destination. |

# Demo flag scenario
Our sales field uses a set of Databricks workspaces to demo your product working with ours to prospective customers.  We believe the new-trial flow is the most compelling demo.  In those workspaces, we'll set demo == true in the Connect API payload and the sales field will delete/re-create connections to demo the product.

## P700
|Topic |Description |
|---|----|
|Explanation |When demo == true, Connect API requests use the P100 flow even if the user or workspace is "existing". |
|When this test case applies |For any partners that implement the Connect API when demo == true in the payload |
|Is the API test automated? |Yes, in DemoFlowTests |
|Expectations when testing manually |From the same workspace, we can continually delete/create the connection and keep seeing new-trial flows. |

# Test-Connection test-hook
This test hook can be used to validate connectivity back to Databricks (often to a SQL Warehouse or Interactive Cluster).

## P800
|Topic |Description |
|---|----|
|Explanation |Create a connection, call test-connection, verify success. |
|When this test case applies |For any partners that implement the Connect API and the Test-Connection test-hook API |
|Is the API test automated? |Yes, in TestConnectionTests.scala |
|Expectations when testing manually |None |

## P801
|Topic |Description |
|---|----|
|Explanation |Create a connection, delete the connection in the partner product, call test-connection, verify failure.|
|When this test case applies |For any partners that implement the Connect API and the Test-Connection test-hook API |
|Is the API test automated? |Yes, in TestConnectionTests.scala |
|Expectations when testing manually |None |

# Connector API (for Ingestion partners)
This API is used to inform Databricks which connectors Ingestion partners use (e.g. Salesforce, Google Analytics).  It is used offline to populate our systems.

## P900
|Topic |Description |
|---|----|
|Explanation |Calling get-connector-list returns valid response |
|When this test case applies |For Ingestion partners that implement the get-connectors-list test-hook API |
|Is the API test automated? |Yes, in GetConnectorTests.scala |
|Expectations when testing manually |None |
