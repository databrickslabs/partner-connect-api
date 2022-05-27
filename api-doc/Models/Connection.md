# Connection
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **redirect\_uri** | **String** | The URL to launch in a new browser tab. This is required on 200/201 OK response. | [default to null] |
| **redirect\_value** | **String** | An enum that identifies the redirect_uri scenario.  This will be used to verify correct behavior in automated testing. | [default to null] |
| **connection\_id** | **String** | Connection id representing the resource the partner provisioned. This field is required when configured_resources is true. | [optional] [default to null] |
| **user\_status** | **String** | Enum that represents whether the partner has seen the user before. | [default to null] |
| **account\_status** | **String** | Enum that represents whether the partner has seen the account (i.e. the company or email domain) before. | [default to null] |
| **configured\_resources** | **Boolean** | A boolean that represents whether the partner configured/persisted the Databricks resources on this Connect API request.  If the value is false and is_connection_established is false, Databricks will clean up the resources it provisioned | [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

