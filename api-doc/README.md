# Documentation for Partner Connect API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://domainnameofpartner*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *AccountApi* | [**deleteAccount**](Apis/AccountApi.md#deleteaccount) | **DELETE** /delete-account | **Test Only** Delete the account related to a given domain. This API should remove all users and connections associated with all users under this domain. This API is only used for automated tests. Partners should only allow this API call on Databricks related test domain names (databricks-test.com and databricks-demo.com). If any other domain is used, api call should fail with 400 Bad Request |
*AccountApi* | [**expireAccount**](Apis/AccountApi.md#expireaccount) | **PUT** /expire-account | **Test Only** Expire account api is used to expire a user trial. After this api is called, the partner is expected to return the redirect uri for handling expired users. The account_status should be set to expired. This api is only used for automated verification of the partner connect flow for expired accounts. This api needs to be limited to databricks test domain names (databricks-test.com and databricks-demo.com). Partners should return 400 bad request if it's called for any other domain name. |
| *ConnectionApi* | [**connect**](Apis/ConnectionApi.md#connect) | **POST** /connect | The Connect API is used to sign-in or sign-up a user with a partner with Databricks resources pre-configured. |
*ConnectionApi* | [**deleteConnection**](Apis/ConnectionApi.md#deleteconnection) | **DELETE** /delete-connection | Delete the connection created by partner. This API is used for automated tests and is optional in the Partner Connect experience. In the Partner Connect experience, Databricks calls this API to notify partners about connection deletion from Databricks's side. |
*ConnectionApi* | [**testConnection**](Apis/ConnectionApi.md#testconnection) | **POST** /test-connection | Test the connection created by calling connect endpoint. This api is currently only used in automated tests. In the future it may be included in the partner connect experience. |
| *DatasourceApi* | [**getConnectors**](Apis/DatasourceApi.md#getconnectors) | **GET** /connectors | Returns list of connectors supported by the partner. This is only applicable to Data ingestion partners. |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [AccountInfo](./Models/AccountInfo.md)
 - [AccountUserInfo](./Models/AccountUserInfo.md)
 - [Auth](./Models/Auth.md)
 - [ConnectRequest](./Models/ConnectRequest.md)
 - [Connection](./Models/Connection.md)
 - [ConnectionInfo](./Models/ConnectionInfo.md)
 - [ConnectionTestResult](./Models/ConnectionTestResult.md)
 - [Connector](./Models/Connector.md)
 - [ConnectorResponse](./Models/ConnectorResponse.md)
 - [DataPermission](./Models/DataPermission.md)
 - [DeleteConnectionRequest](./Models/DeleteConnectionRequest.md)
 - [DeleteConnectionResponse](./Models/DeleteConnectionResponse.md)
 - [ErrorResponse](./Models/ErrorResponse.md)
 - [ObjectPermission](./Models/ObjectPermission.md)
 - [PartnerConfig](./Models/PartnerConfig.md)
 - [PartnerConfig_endpoints](./Models/PartnerConfig_endpoints.md)
 - [PartnerConfig_test_workspace_detail](./Models/PartnerConfig_test_workspace_detail.md)
 - [ResourceToProvision](./Models/ResourceToProvision.md)
 - [TestResult](./Models/TestResult.md)
 - [UserInfo](./Models/UserInfo.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

<a name="basicAuth"></a>
### basicAuth

- **Type**: HTTP basic authentication

