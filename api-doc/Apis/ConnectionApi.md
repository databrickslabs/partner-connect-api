# ConnectionApi

All URIs are relative to *https://domainnameofpartner*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**connect**](ConnectionApi.md#connect) | **POST** /connect |  |
| [**deleteConnection**](ConnectionApi.md#deleteConnection) | **DELETE** /delete-connection |  |
| [**testConnection**](ConnectionApi.md#testConnection) | **POST** /test-connection |  |


<a name="connect"></a>
# **connect**
> Connection connect(User-Agent, ConnectRequest, Accept-Language, Content-Type)



    The Connect API is used to sign-in or sign-up a user with a partner with Databricks resources pre-configured.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks] |
| **ConnectRequest** | [**ConnectRequest**](../Models/ConnectRequest.md)| The connection payload. | |
| **Accept-Language** | **String**| Preferred language | [optional] [default to en-US] |
| **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8] |

### Return type

[**Connection**](../Models/Connection.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteConnection"></a>
# **deleteConnection**
> DeleteConnectionResponse deleteConnection(User-Agent, DeleteConnectionRequest, Accept-Language, Content-Type)



    Delete the connection created by partner. This API is used for automated tests and is required in the Partner Connect experience. In the Partner Connect experience, Databricks calls this API to notify partners about connection deletion from Databricks&#39;s side.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks] |
| **DeleteConnectionRequest** | [**DeleteConnectionRequest**](../Models/DeleteConnectionRequest.md)| The delete connection payload. | |
| **Accept-Language** | **String**| Preferred language | [optional] [default to en-US] |
| **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8] |

### Return type

[**DeleteConnectionResponse**](../Models/DeleteConnectionResponse.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="testConnection"></a>
# **testConnection**
> ConnectionTestResult testConnection(User-Agent, ConnectionInfo, Accept-Language, Content-Type)



    Test the connection created by calling connect endpoint. This api is currently only used in automated tests. In the future it may be included in the partner connect experience.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks] |
| **ConnectionInfo** | [**ConnectionInfo**](../Models/ConnectionInfo.md)|  | |
| **Accept-Language** | **String**| Preferred language | [optional] [default to en-US] |
| **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8] |

### Return type

[**ConnectionTestResult**](../Models/ConnectionTestResult.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

