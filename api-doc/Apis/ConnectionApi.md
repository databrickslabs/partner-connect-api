# ConnectionApi

All URIs are relative to *https://domainnameofpartner*

Method | HTTP request | Description
------------- | ------------- | -------------
[**connect**](ConnectionApi.md#connect) | **POST** /connect | 
[**deleteConnection**](ConnectionApi.md#deleteConnection) | **DELETE** /delete-connection | 
[**testConnection**](ConnectionApi.md#testConnection) | **POST** /test-connection | 


<a name="connect"></a>
# **connect**
> Connection connect(User-Agent, ConnectRequest, Accept-Language, Content-Type)



    The Connect API is used to sign-in or sign-up a user with a partner with Databricks resources pre-configured.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks]
 **ConnectRequest** | [**ConnectRequest**](../Models/ConnectRequest.md)| The connection payload. |
 **Accept-Language** | **String**| Prefered lanaguage | [optional] [default to en-US]
 **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8]

### Return type

[**Connection**](../Models/Connection.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="deleteConnection"></a>
# **deleteConnection**
> deleteConnection(User-Agent, ConnectionInfo, Accept-Language, Content-Type)



    Delete the connection created by partner. Currently this API is only used for automated tests.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks]
 **ConnectionInfo** | [**ConnectionInfo**](../Models/ConnectionInfo.md)| The test connection payload. |
 **Accept-Language** | **String**| Prefered lanaguage | [optional] [default to en-US]
 **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8]

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="testConnection"></a>
# **testConnection**
> ConnectionTestResult testConnection(User-Agent, ConnectionInfo, Accept-Language, Content-Type)



    Test the connection created by calling connect endpoint.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks]
 **ConnectionInfo** | [**ConnectionInfo**](../Models/ConnectionInfo.md)|  |
 **Accept-Language** | **String**| Prefered lanaguage | [optional] [default to en-US]
 **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8]

### Return type

[**ConnectionTestResult**](../Models/ConnectionTestResult.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

