# ObjectPermission
## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **String** | Databricks object and endpoint permission https://docs.databricks.com/dev-tools/api/latest/permissions.html   * Token — Manage which users can create or use tokens.   * Password — Manage which users can use password login when SSO is enabled.   * Cluster — Manage which users can manage, restart, or attach to clusters.   * Pool — Manage which users can manage or attach to pools. Some APIs and documentation refer to pools as instance pools.   * Job — Manage which users can view, manage, trigger, cancel, or own a job.   * DLT_Pipeline — Manage which users can view, manage, run, cancel, or own a Delta Live Tables pipeline.   * Notebook — Manage which users can read, run, edit or manage a notebook.   * Directory — Manage which users can read, run, edit, or manage all notebooks in a directory.   * MLflow_Experiment — Manage which users can read, edit, or manage MLflow experiments.   * MLflow_Registered_Model — Manage which users can read, edit, or manage MLflow registered models.   * SQL_Endpoint — Manage which users can use or manage SQL endpoints.   * Repo — Manage which users can read, run, edit, or manage a repo.  | [default to null]
**level** | **String** | Permission level | [default to null]

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

