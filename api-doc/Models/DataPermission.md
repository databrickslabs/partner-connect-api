# DataPermission
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **permission** | **String** | Data permission https://docs.databricks.com/security/access-control/table-acls/object-privileges.html#privileges * SELECT: gives read access to an object. * CREATE: gives ability to create an object (for example, a table in a schema). * MODIFY: gives ability to add, delete, and modify data to or from an object. * USAGE: does not give any abilities, but is an additional requirement to perform any action on a schema object. * READ_METADATA: gives ability to view an object and its metadata. * CREATE_NAMED_FUNCTION: gives ability to create a named UDF in an existing catalog or schema. * MODIFY_CLASSPATH: gives ability to add files to the Spark class path. * ALL PRIVILEGES: gives all privileges (is translated into all the above privileges).\&quot;  | [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

