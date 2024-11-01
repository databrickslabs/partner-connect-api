# PartnerConfig
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **api\_version** | **String** | The partner api version supported by the partner. Format major.minor.patch | [default to null] |
| **name** | **String** | Name of the partner | [default to null] |
| **description\_blurb** | **String** | Description text to show in the UI. | [default to null] |
| **category** | **String** | The partner category. | [default to null] |
| **base\_url** | **String** | Base URL for the partner service | [default to null] |
| **endpoints** | [**PartnerConfig_endpoints**](PartnerConfig_endpoints.md) |  | [default to null] |
| **sign\_up\_page** | **String** | Sign up web page | [default to null] |
| **privacy\_policy** | **String** | Link to privacy policy | [default to null] |
| **terms\_of\_service** | **String** | Terms of service | [default to null] |
| **logo\_file** | **String** | Logo file (Format: .png or .svg; Dimensions: W px * H px where W&#x3D;~4.4*H. The multiplier is roughly 4.4, but 4.0~4.5 should be acceptable as some of the partner names may be long and require more horizontal space.) | [default to null] |
| **icon\_file** | **String** | Square icon file (Format: .png or .svg; Dimensions: N px* N px where N&gt;&#x3D;128) | [default to null] |
| **resources\_to\_provision** | [**List**](ResourceToProvision.md) | List of resources to provision | [default to null] |
| **data\_permissions** | [**List**](DataPermission.md) | Required list of Data ACLs | [optional] [default to null] |
| **object\_permissions** | [**List**](ObjectPermission.md) | Required list of Object ACLs | [optional] [default to null] |
| **supported\_clouds** | **List** | Supported clouds. | [default to null] |
| **hostnames** | **List** | Redirect URL allow-list hostnames. | [default to null] |
| **new\_user\_action** | **String** | Action taken by partner when a new user tries to join an existing account. Options are auto_add, invite, not_found, error. auto_add and invite should return 200 OK with redirect url. not_found should return 404 and error should return 500 | [default to null] |
| **require\_manual\_signup** | **Boolean** | True if the partner requires a manual signup after connect api is called. When set to true, connect api call with is_connection_established (sign in) is expected to return 404 account_not_found or connection_not_found until the user completes the manual signup step. | [optional] [default to null] |
| **trial\_type** | **String** | Enum describing the type of trials the partner support. Partners can chose to support trial account expiration at the individual user or account level. If trial level is user, expiring one user connection should not expire another user in the same account. | [optional] [default to null] |
| **supports\_demo** | **Boolean** | True if partner supports the demo flag in the connect api call. | [optional] [default to null] |
| **auth\_options** | **List** | The available authentication methods that a partner can use to authenticate with Databricks. If it is not specified, `AUTH_PAT` will be used. The allowed options include <ul><li><b>AUTH_PAT</b></li><li><b>AUTH_OAUTH_M2M</b></li><li><b>AUTH_OAUTH_U2M</b></li></ul>| [optional] [default to null] |
| **test\_workspace\_detail** | [**PartnerConfig_test_workspace_detail**](PartnerConfig_test_workspace_detail.md) |  | [optional] [default to null] |
| **is_published_app** |  **Boolean**  | True if the partner app is registered as Databricks published OAuth app | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

