# API Specifications

<!-- TOC -->
- [API overview](ApiSpecifications.md#api-overview)
    - [OpenApi Specification](ApiSpecifications.md#openapi-specification)
    - [Headers](ApiSpecifications.md#headers)
      - [Request Headers](ApiSpecifications.md#request-headers)
      - [Response Headers](ApiSpecifications.md#response-headers)
- [Connection API (All Partners)](ApiSpecifications.md#connection-api-all-partners)
    - [Connect](ApiSpecifications.md#connect)
        - [Sequence diagram](ApiSpecifications.md#sequence-diagram)
        - [Redirect URL explanation](ApiSpecifications.md#redirect-url-explanation)
        - [Example partner scenarios](ApiSpecifications.md#example-partner-scenarios)
        - [A note on established connections](ApiSpecifications.md#a-note-on-established-connections)
        - [API Specification](ApiSpecifications.md#api-specification)
            - [Databricks data model](ApiSpecifications.md#databricks-data-model)
- [Datasource API (Ingestion Partners)](ApiSpecifications.md#datasource-api-ingestion-partners)
    - [Get Connector List](ApiSpecifications.md#get-connector-list)
        - [Get Connector List API Example](ApiSpecifications.md#get-connector-list-api-example)
- [Regression Testing APIs](ApiSpecifications.md#regression-testing-apis)
    - [Test Connection API](ApiSpecifications.md#test-connection-api)
    - [Delete Account API](ApiSpecifications.md#delete-account-api)
    - [Delete Connection API](ApiSpecifications.md#delete-connection-api)
    - [Expire Account API](ApiSpecifications.md#expire-account-api)
    - [Enable Databricks to demo partner products with Partner Connect](ApiSpecifications.md#enable-databricks-to-demo-partner-products-with-partner-connect)
        - [Demo boolean flag](ApiSpecifications.md#demo-boolean-flag)
<!-- TOC -->

# API overview

This page details the technical specifications of the APIs that partners implement.  Also see the [Technical FAQ](TechnicalFAQ.md) for common questions.

## OpenApi Specification

[OpenApi specification](openapi/partner-connect-2.0.yaml)

## Headers

###### Request Headers

In the API requests, Databricks will include the following request headers:

```
"User-Agent": "databricks"
"Authorization": "Basic <base64 user:pwd>" [user:pwd provided by partner to Databricks for authentication]
"Content-Type": "application/json"
"Accept-Language": "user language" [en-US]
```

###### Response Headers

Partners need to include the following headers in all api responses that return JSON payload.

```
"Content-Type": "application/json"
```

Databricks will be authenticating using basic authentication with the partners.

# Connection API (All Partners)

## Connect

The Connect API is used to sign-in or sign-up a user with a partner with Databricks resources pre-configured.

#### Sequence diagram

The order of events when connecting Databricks to a partner is as follows:

1. The user clicks the Partner tile.
2. The user confirms the Databricks resources that will be provisioned for the connection (e.g. the Service Principal, the PAT, the SQL Warehouse).
3. The user clicks Connect.
    1. Databricks calls the partner&#39;s **Connect API** with all of the Databricks data that the partner needs.
    2. The partner provisions any accounts and resources needed. (e.g. persisting the Databricks workspace\_id, provisioning a Databricks output node).
    3. The partner responds with
        1. **HTTP status code** - for determining success, Databricks failure, and partner failure.
        2. **Redirect URL** - for Databricks to launch in a new browser tab.
        3. **Additional data** - see below.
4. Databricks launches the **Redirect URL** in a new browser tab.

![](img/image5.png)

#### Redirect URL explanation

The Redirect URL can be customized by the partner to handle different cases. Partners can embed arbitrary data (e.g. user info) into the URL. As a typical example, a partner may choose to implement the following URLs. Superscripts denote options for the same scenario depending on the partner&#39;s capabilities.

- [www.partner.com/create-trial](http://www.partner.com/create-trial)
    - Used when the partner has never seen the user or the account before.
    - ¹Used when the account has an expired trial (and the partner allows multiple trials per account)
- [www.partner.com/sign-in](http://www.partner.com/sign-in)
    - ²Used when the partner has never seen the user, but has seen the account, and can automatically provision the user.
    - Used when the partner has seen the user and the account.
- [www.partner.com/contact-your-admin](http://www.partner.com/contact-your-admin)
    - ²Used when the partner has never seen the user, but has seen the account, and cannot automatically provision the user.
- [www.partner.com/purchase-product](http://www.partner.com/purchase-product)
    - ¹Used when the account has an expired trial (and the partner does not allow multiple trials per account)

#### Example partner scenarios

|Scenario                                                           |Partner operations during Connect API|Response                                                                                                                                                                                                                                                                                                 |
|-------------------------------------------------------------------|-------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|New user, new account                                              |<ul><li>Set up free trial for new account</li><li>Configure Databricks integration in account</li></ul>|**Status_code** = 200<br />**Connection_id** = abcd<br />**Configured_resources** = true<br />**User_status** = new<br />**Account_status** = new<br />**Redirect Value** = create_trial<br />**Redirect URL** = www.partner.com/create-trial                                                                                                                      |
|Expired account                                                    |<ul><li>If the partner requires the product to be purchased…<ul><li>N/A</li></ul><li>If it allows another free trial</li><ul><li>Set up free trial</li><li>Configure Databricks integration</li></ul></ul>|**Status_code** = 200<br />**Connection_id** = omitted OR abcd<br />**Configured_resources** = false OR true<br />**User_status** = new OR existing<br />**Account_status** = expired<br />**Redirect Value** = purchase_product OR create_trial<br />**Redirect URL** = www.partner.com/purchase-product OR www.partner.com/create-trial                          |
|New user, existing account                                         |<ul><li>If the user can be added dynamically…</li><ul><li>Add the user</li><li>Configure Databricks integration</li></ul><li>If not…<ul><li>No action OR</li><li>Set up free trial for new user and configure Databricks integration</li></ul></ul>|**Status_code** = 200<br />**Connection_id** = abcd or omitted<br />**Configured_resources** = true OR false<br />**User_status** = new<br />**Account_status** = active<br />**Redirect Value** = sign_in OR contact_admin OR create_trial<br />**Redirect URL** =  www.partner.com/sign-in  OR www.partner.com/contact-your-admin OR www.partner.com/create-trial|
|Existing user, existing account                                    |<ul><li>Configure Databricks integration</li></ul>     |**Status_code** = 200<br />**Connection_id** = abcd or omitted<br />**Configured_resources** = true OR false<br />**User_status** = existing<br />**Account_status** = active<br />**Redirect Value** = sign_in<br />**Redirect URL** =  www.partner.com/sign-in                                                                                                   |

#### A note on established connections

Databricks and the partner may have different values for whether the connection is established. A user may delete the connection on either the Databricks-side or the partner-side causing this mismatch. Here&#39;s what should happen with the Connect API in each of the 4 cases.

1. If Databricks has no connection configured, it will send a payload with **is\_connection\_established** set to **false**.
    1. If the partner has no connection configured, they will configure the connection.
    2. If the partner has a connection configured, they will configure a new, separate connection. This can happen if a connection was previously deleted on the Databricks-side or if a connection was manually configured from the partner to Databricks outside of Partner Connect.
2. If Databricks has a connection configured, it will send a payload with **is\_connection\_established** set to **true**.
    1. If the partner has no connection configured, the partner responds with 404 connection\_not\_found so that Databricks can tell the user to take action.
    2. If the partner has a connection configured, no new connection needs to be configured.

#### API Specification

##### Databricks data model

- A company or an organization has a Databricks **account**. We do not provide an identifier that represents the account to partners.
- An account can have multiple Databricks **workspaces**. A workspace is the logical container and isolation boundary for Databricks resources (e.g. SQL Warehouses). Databricks\_organization\_id and workspace\_id are interchangeable as the appropriate identifier. Partner Connect is accessed within a workspace.
- A **user** can belong to multiple workspaces. A user has a unique email address. A user&#39;s identifier is databricks\_user\_id which is unique within a cloud (e.g. Azure).
- Partner Connect stores 0 or 1 **connections** per partner per workspace.

Databricks will pass the below standard fields to your API. In order to be in Partner Connect, we need your API to support all of the mandatory fields, meaning that even if you receive information you don&#39;t need, you shouldn&#39;t return an error. If there are some additional fields that you would like us to support, do let us know through your partner representative.

```
POST <partners/databricks/v1/connect>: [example, can be customized]
{
  "user_info": {
     "email": "john.doe@databricks.com", [valid email address]
     "first_name": "John", [Non-null String, may be empty string]
     "last_name": "Doe", [Non-null String, may be empty string]
     "databricks_user_id": 1234567890, [data-type is long]
     "databricks_organization_id": 1234567890, [data-type is long]
     "is_connection_established" : true|false
     "auth": { [Only present if is_connection_established is false]
       "personal_access_token": "dapi..."
       // or
       "oauth_token": ..., [optional, reserved for future use]
       "oauth_scope": ... [optional, reserved for future use]
      }
  }
  "hostname": "organization.cloud.databricks.com",
  "port": 443,
  "workspace_url": "https://[organization/prefix-workspaceid/string].cloud.databricks.com/?o=12345677890",
  "http_path": "sql/protocolv1/o/0/0222-185802-deny427", [optional, set if is_sql_warehouse is true]
  "jdbc_url": "jdbc:spark://organization.cloud.databricks.com:443/...", [optional, set if is_sql_warehouse is true, used for legacy JDBC spark driver]
  "databricks_jdbc_url": "jdbc:databricks://organization.cloud.databricks.com:443/...", [optional, set if is_sql_warehouse is true, used for new JDBC databricks driver]
  "connection_id": "7f2e4c43-9714-47cf-9011-d8148eaa27a2", [example, optional, only present when is_connection_established is true]
  "workspace_id": 1234567890, [same as user_info.databricks_organization_id]
  "demo": true|false, [see Demos section below]
  "cloud_provider": "azure", [or aws or gcp]
  "is_free_trial": true|false, [is Databricks free trial]
  "staging_location": "<cloud>://<location_1>", [optional, unused and reserved for future use]
  "destination_location": "<cloud>://<location_2>", [optional]
  "catalog_name": "my_catalog",[optional, it could be a custom name if using Unity Catalog, or "hive_metastore" if not.]
  "database_name": "default database to use", [optional, unused and reserved for future use]
  "cluster_id": "0222-185802-deny427", [optional: set only if jdbc/interactive cluster is required.]
  "is_sql_endpoint" : true|false, [optional: same value as is_sql_warehouse]
  "is_sql_warehouse": true|false, [optional: set if cluster_id is set. Determines whether cluster_id refers to Interactive Cluster or SQL Warehouse]
  "data_source_connector": "Oracle", [optional, unused and reserved for future use: for data connector tools, the name of the data source that the user should be referred to in their tool]
  "service_principal_id": "a2a25a05-3d59-4515-a73b-b8bc5ab79e31" [optional, the UUID (username) of the service principal identity]
}
```

**Successful Responses:**

```
Status Code: 200
{
  "redirect_uri": "https://...",
  "redirect_value": "create_trial", [example]
  "connection_id": "7f2e4c43-9714-47cf-9011-d8148eaa27a2", [example, optional, see below]
  "user_status": "new", [example]
  "account_status": "existing", [example]
  "configured_resources": true|false
 }
```
Return values:

1. **redirect\_uri** - the URL to launch in a new browser tab.  Please note that we validate the hostname in the redirect_uri to prevent a redirect attack.  If your redirect_uri will have a different hostname than your Connect API, we'll need to safelist that.
2. **redirect\_value** - a String that identifies the redirect\_uri scenario. This will be used to verify correct behavior in automated testing. Valid values are &quot;create\_trial&#39;, &quot;purchase\_product&quot;, &quot;sign\_in&quot;, &quot;contact\_admin&quot;, and &quot;not\_applicable&quot;.
3. **connection\_id** - a String that identifies the connection in the Partner&#39;s system. This may be used as part of future improvements (See Optional section below).
    1. If **is\_connection\_established** is true in the request, **connection\_id** should be omitted in the response.
    2. If **is\_connection\_established** is false and **configured\_resources** is true, **connection\_id** must be present in the response
    3. If **is\_connection\_established** is false and **configured\_resources** if false, **connection\_id** should be omitted in the response.
4. **user\_status** - a String that represents whether the partner has seen the user before. Valid values are &quot;new&quot;, &quot;existing&quot;, and &quot;not\_applicable&quot;.
5. **account\_status** - a String that represents whether the partner has seen the account (i.e. the company or email domain) before. Valid values are &quot;new&quot;, &quot;active&quot;, &quot;expired&quot;, and &quot;not\_applicable&quot;.
6. **configured\_resources** - a boolean that represents whether the partner configured/persisted the Databricks resources on this Connect API request.
    1. If **is\_connection\_established** is true, **configured\_resources** must be set, but will be ignored.
    2. If **is\_connection\_established** is false and **configured\_resources** is false, Databricks will delete the resources it provisioned.

**Failure Responses:**

All failure responses contain the same 3 fields:

1. **error\_reason** - Required; must be set to the appropriate enum value.
2. **display\_message** - Not required; will be displayed to the user if set in the case of 404 or 500
3. **debugging\_message** - Not required; will be logged if set.

_Bad request_

Thrown when Databricks provides a malformed request to the partner. This should never happen.

```
Status Code: 400
{
  "error_reason": "bad_request",
  "display_message": "foobar", [optional, not displayed for 400]
  "debugging_message": "foobar" [optional]
 }
```

_Bad credentials_

Thrown when Databricks provides the wrong credentials to the partner. This should never happen, but may happen in cases where credentials need to be rotated. Databricks will present the link to the sign in page for the partner.

```
Status Code: 401
{
  "error_reason": "unauthorized",
  "display_message": "foobar", [optional, not displayed for 401]
  "debugging_message": "foobar" [optional]
}
```

_Account or Connection not found_

Should only be possible if Databricks sends with **is\_connection\_established** set to **true**.

1. If the connection does not exist on the partner-side, return a 404 with **error\_reason** set to **connection\_not\_found**. This occurs when a connection has been deleted on the partner side. The user will be directed to delete the connection in Databricks and re-create.
2. If the account does not exist on the partner-side, return a 404 with **error\_reason** set to **account\_not\_found**. This occurs when a partner wishes to create separate connections from a single Databricks workspace to multiple partner workspaces (e.g. one for each user). The user will be directed to contact their admin for an invite to an existing workspace or to delete the existing connection and recreate, which is a destructive operation. Improvements are planned for this workflow.

```
Status Code: 404
{
  "error_reason": "account_not_found", [or “connection_not_found”]
  "display_message": "foobar", [optional, will be displayed if present]
  "debugging_message": "foobar" [optional]
}
```

_Unexpected failure_

For any other unexpected failures, the partner will return a 500. Databricks will retry the request 3 times with exponential backoff: first request after 1 second, second request after 2 seconds, and third request after 4 seconds. If Databricks continues to receive a 500, Databricks will present the resources that are created in the UI, but provide a link to the regular sign in page for the partner.

```
Status Code: 500
{
  "error_reason": "general_error",
  "display_message": "foobar", [optional, will be displayed if present]
  "debugging_message": "foobar" [optional]
}
```

# Datasource API (Ingestion Partners)

## Get Connector List

The partner should provide a REST GET api that returns the list of connectors they support. Partners are responsible for using the Databricks provided list of identifiers to map the data source connectors.

[Connectors List](openapi/connectors.csv)

#### Get Connector List API Example

`GET /partners/databricks/v1/connectors?pagination\_token=101`

Response:

```
{
  "connectors": [{
     "identifier": "DATABRICKS_CONNECTOR1_ID",
     "name": "connector name1",
     "type": "source", [or “target”]
     "description": "Connects to ..." [optional]
   },
   {
     "identifier": "DATABRICKS_CONNECTOR2_ID",
     "name": "connector name1",
     "type": "source", [or “target”]
     "description": "Connects to ..." [optional]
   }, ...
 ],
 "Pagination_token": 101 // A token to continue listing
}
```

# Regression Testing APIs
The following APIs are required for automated certification tests. Once partners implement these APIs, they can use the [partner certification tests](README.md#partner-connect-certification) to validate the user scenarios.

## Test Connection API

This API is currently only used in automated tests. In the future it may be included in the partner connect experience.

```
POST <partners/databricks/test-connection>:
{
  "connection_id": "7f2e4c43-9714-47cf-9011-d8148eaa27a2",
  "cloud_provider": "azure", [or aws or gcp]
  "databricks_organization_id": 123456789012345678,
}
```

**Successful Responses:**

```
Status Code: 200
{
  "test_results": [
    {
        "test_name": "Connectivity",
        "status": "SUCCESS",
        "message": "Successfully connected to host"
    },
    {  
        "test_name": "Permissions",
        "status": "FAILED",
        "message": "WRITE permission check failed to database"
    }   
  ]
 }
```

**Failure Responses:**

_Connection not found._

```
Status Code: 404
{
  "error_reason": "connection_not_found"
  "display_message": "foobar", [optional, will be displayed if present]
  "debugging_message": "foobar" [optional]
}
```

_Unexpected failure_

```
Status Code: 500
{
  "error_reason": "general_error",
  "display_message": "foobar", [optional]
  "debugging_message": "foobar" [optional]
}
```

## Delete Account API

This API will clean up all the resources provisioned for a given account based on the domain name,
cloud provider and org id (workspace_id).
This API needs to be limited to databricks test domain names (databricks-test.com and databricks-demo.com).
Delete api should throw 400 Bad requests if it&#39;s called for any other domain name.
This API is currently only used in automated tests.

```
DELETE <partners/databricks/account>:
{
  "domain": "abc123.databricks-test.com",
 "cloud_provider": "azure", [or aws or gcp]
 "databricks_organization_id": 123456789012345678,
}
```

**Successful Responses:**

```
Status Code: 200
```

**Failure Responses:**

_Bad Request_

```
Status Code: 400
{
  "error_reason": "bad_request",
  "display_message": "foobar", [optional]
  "debugging_message": "foobar" [optional]
}
```

_Unexpected failure_

```
Status Code: 500
{
  "error_reason": "general_error",
  "display_message": "foobar", [optional]
  "debugging_message": "foobar" [optional]
}
```

## Delete Connection API

This API should clean up a specific connection id in a given org id (workspace_id) and cloud provider.
This API is used in automation testing and is optional for the Partner Connect experience.

### Partner Connect Experience
In the Partner Connect experience, Databricks will use the Delete Connection API to notify partners about connection deletion from Databricks's side.
If the partner has specified a delete-connection endpoint, Databricks will make a
call to the Delete Connection API after deleting associated resources on the Databricks side.

## Automated Testing
If the partner confirms resource deletion through the `resources_deleted` resource status, automated testing will be done to ensure that new connections can be made.

```
DELETE <partners/databricks/connection>:
{
  "connection_id": "7f2e4c43-9714-47cf-9011-d8148eaa27a2",
 "cloud_provider": "azure", [or aws or gcp]
 "databricks_organization_id": 123456789012345678,
}
```

**Successful Responses:**

The field **resource_status** is a String which identifies the action that the partner-side took, if any. Valid values are "resources_deleted",  "resources_pending_deletion", or "deletion_acknowledged".

```Status Code: 200
{
  "resource_status": "resources_deleted" [or resources_pending_deletion or deletion_acknowledged]
}
```

**Failure Responses**:

_Bad credentials_

Thrown when Databricks provides the wrong credentials to the partner. This should never happen, but may happen in cases where credentials need to be rotated.

```
Status Code: 401
{
  "error_reason": "unauthorized",
  "display_message": "foobar", [optional]
  "debugging_message": "foobar" [optional]
}
```

_Connection not found_

If the connection does not exist on the partner-side, return a 404 with **error\_reason** set to **connection\_not\_found**. This occurs when a connection has already been deleted on the partner side.
```
Status Code: 404
{
  "error_reason": “connection_not_found”
  "display_message": "foobar", [optional]
  "debugging_message": "foobar" [optional]
}
```

_Unexpected failure_

For any other unexpected failures, the partner will return a 500. Databricks will retry the request 3 times with exponential backoff: first request after 1 second, second request after 2 seconds, and third request after 4 seconds.

```
Status Code: 500
{
  "error_reason": "general_error",
  "display_message": "foobar", [optional]
  "debugging_message": "foobar" [optional]
}
```

## Expire Account API

The expire account API is used to expire a user trial. After this API is called, the partner is expected to return the redirect uri for handling expired users.
The **account\_status** should be set to **expired**.
This API is only used for automated verification of the partner connect flow for expired accounts.
This API needs to be limited to databricks test domain names (databricks-test.com and databricks-demo.com).
Partners should return 400 bad requests if it&#39;s called for any other domain name.
This is an optional API if the partner doesn&#39;t support time-based account expiry.

```
PUT <partners/databricks/useraccount>:
{
 "email": "test@test.com",
 "databricks_user_id": 123456789012345678,
 "cloud_provider": "azure", [or aws or gcp]
 "databricks_organization_id": 123456789012345678,
}
```

**Successful Responses:**

```
Status Code: 200
```

**Failure Responses:**

_Unexpected failure_

```
Status Code: 500
{
  "error_reason": "general_error",
  "display_message": "foobar", [optional]
  "debugging_message": "foobar" [optional]
}
```

## Enable Databricks to demo partner products with Partner Connect

We would like to demo select partner products to our field team and in Partner Connect product demonstrations.
In order to do this, we need to ensure that partners return the Create Trial experience even when the same email, account,
and workspace are repeatedly used.

#### Demo boolean flag

The Connect API request includes a &quot; **demo**&quot; boolean flag that we will set to true only when demo-ing your product. When true, ensure we get the Create Trial flow, even if you&#39;ve seen the user, account, and/or Databricks workspace before.  Handling concurrent requests is not required.
