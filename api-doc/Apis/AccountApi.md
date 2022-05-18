# AccountApi

All URIs are relative to *https://domainnameofpartner*

Method | HTTP request | Description
------------- | ------------- | -------------
[**deleteAccount**](AccountApi.md#deleteAccount) | **DELETE** /delete-account | 
[**expireAccount**](AccountApi.md#expireAccount) | **PUT** /expire-account | 


<a name="deleteAccount"></a>
# **deleteAccount**
> deleteAccount(User-Agent, AccountInfo, Accept-Language, Content-Type)



    Delete the account related to a given domain. This API should remove all users and connections associated with all users under this domain. This API is only used for automated tests. Partners should only allow this API call on Databricks related test domain names (databricks-test.com and databricks-demo.com). If any other domain is used, api call should fail with 400 Bad Request

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks]
 **AccountInfo** | [**AccountInfo**](../Models/AccountInfo.md)|  |
 **Accept-Language** | **String**| Prefered lanaguage | [optional] [default to en-US]
 **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8]

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="expireAccount"></a>
# **expireAccount**
> expireAccount(User-Agent, AccountUserInfo, Accept-Language, Content-Type)



    Expire account api is used to expire a user trial. After this api is called, the partner is expected to return the redirect uri for handling expired users. The account_status should be set to expired. This api is only used for automated verification of the partner connect flow for expired accounts. This api needs to be limited to databricks test domain names (databricks-test.com and databricks-demo.com). Partners should return 400 bad request  if it&#39;s called for any other domain name.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **User-Agent** | **String**| The user agent making the call. This will be set to databricks. | [default to databricks] [enum: databricks]
 **AccountUserInfo** | [**AccountUserInfo**](../Models/AccountUserInfo.md)|  |
 **Accept-Language** | **String**| Prefered lanaguage | [optional] [default to en-US]
 **Content-Type** | **String**| Content type | [optional] [default to application/json; charset&#x3D;utf-8]

### Return type

null (empty response body)

### Authorization

[basicAuth](../README.md#basicAuth)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

