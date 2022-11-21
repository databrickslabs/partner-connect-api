# ConnectRequest
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **user\_info** | [**UserInfo**](UserInfo.md) |  | [default to null] |
| **connection\_id** | **String** | The partner connection id returned on initial connection. This will only be set when is_connection_established is true | [optional] [default to null] |
| **hostname** | **String** | Hostname for the Databricks connection. | [default to null] |
| **port** | **Integer** | Port for the Databricks connection. | [default to null] |
| **workspace\_url** | **String** | Workspace Url to access the Databricks connection. | [default to null] |
| **http\_path** | **String** | Http path for the Databricks connection. Only present if is_sql_endpoint is true. | [optional] [default to null] |
| **jdbc\_url** | **String** | Legacy Spark JDBC connection url | [optional] [default to null] |
| **databricks\_jdbc\_url** | **String** | New Databricks JDBC driver connection url | [optional] [default to null] |
| **workspace\_id** | **Long** | Workspace id for the Databricks connection. Same as the user_info.databricks_organization_id | [default to null] |
| **demo** | **Boolean** | True if this is a demo experience. | [default to null] |
| **cloud\_provider** | **String** | The cloud provider for the Databricks workspace. | [default to null] |
| **is\_free\_trial** | **Boolean** | Flag to indicate if this is a Databricks free trial. | [default to null] |
| **staging\_location** | **String** | Optional staging location URI.  Unused today. | [optional] [default to null] |
| **destination\_location** | **String** | Optional destination location URI | [optional] [default to null] |
| **catalog\_name** | **String** | Optional catalog name. It could be a custom name if using Unity Catalog, or \&quot;hive_metastore\&quot; if not.  Note that Databricks APIs often require identifiers like this to be escaped with backticks if there are special characters in it. | [optional] [default to null] |
| **database\_name** | **String** | Optional Database name.  Unused today. | [optional] [default to null] |
| **cluster\_id** | **String** | Sql endpoint id if is_sql_endpoint is true otherwise cluster id. | [optional] [default to null] |
| **is\_sql\_endpoint** | **Boolean** | This is using legacy name. SQL endpoint is now SQL warehouse. This field should be the same as is_sql_warehouse. | [optional] [default to null] |
| **is\_sql\_warehouse** | **Boolean** | Determines whether cluster_id refers to Interactive Cluster or SQL warehouse. | [optional] [default to null] |
| **data\_source\_connector** | **String** | For data connector tools, the name of the data source that the user should be referred to in their tool.  Unused today. | [optional] [default to null] |
| **service\_principal\_id** | **String** | The UUID (username) of the service principal identity that a partner product can use to call Databricks APIs. Note the format is different from the databricks_user_id field in user_info. | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

