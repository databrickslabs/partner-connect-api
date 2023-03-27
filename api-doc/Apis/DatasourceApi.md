# DatasourceApi

All URIs are relative to *https://domainnameofpartner*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getConnectors**](DatasourceApi.md#getConnectors) | **GET** /connectors |  |


<a name="getConnectors"></a>
# **getConnectors**
> ConnectorResponse getConnectors(User-Agent, Accept-Language, Content-Type, pagination\_token)



    Returns list of connectors supported by the partner. This is only applicable to Data ingestion partners.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks] |
| **Accept-Language** | **String**| Preferred language | [optional] [default to en-US] |
| **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8] |
| **pagination\_token** | **String**| Optional pagination token to get more results. | [optional] [default to null] |

### Return type

[**ConnectorResponse**](../Models/ConnectorResponse.md)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

