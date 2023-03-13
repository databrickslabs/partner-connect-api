## Frequently Asked Questions (FAQs)
### What's the Databricks organizational model?
- **Public Cloud**: Databricks essentially runs 3 independent instances of Databricks: 1 in AWS, 1 in Azure, and 1 in GCP.  A user will have a different databricks_user_id in each cloud.  It's uncommon for a customer to use more than 1 Public Cloud instance of Databricks.
- **Databricks Account:** This is the customer-level object. Each customer has only 1 account. Customers can have multiple workspaces in an account.
- **Databricks Workspace:** This is the product-level object that users interact with when they use Databricks. All users, user-created content (for example dashboards, jobs, and notebooks), and access controls for that content are part of a workspace. Customer data is not tied to a workspace (with the exception of hive_metastore), and the same data can be made available in multiple workspaces. API calls from partners are made to a workspace.
- **Databricks User:** A user's username is their email address.  They can belong to multiple Accounts, as well as multiple Workspaces within an Account.


### What's the Databricks data model?
- **Unity Catalog (UC) Metastore:** A Databricks Account has multiple metastores.  Metastore are assigned to Workspaces.  A Metastore can be assigned to multiple Workspaces.  A Workspace can only have one Metastore assigned.  A Metastore contains many Catalogs.
- **Catalog:** A logical container for schemas/databases.
- **Schemas/Databases:** A logical container for tables/views/functions.
- **Table:** Metadata (e.g. schema) that includes the physical location of the data in cloud storage.
- **Hive Metastore (or hive_metastore or HMS):** The open-source metastore used before Unity Catalog was released in August, 2022.  A logical container for schemas/databases.  There is 1 HMS per Workspace.  HMS and UC run side-by-side.  From the customer's point of view, hive_metastore is just another catalog within UC.

### What can Databricks configure on a per-partner basis?
- **Unity Catalog support:** Whether to show a catalog-selector for partners that read/write data to the Lakehouse.
- **Supported Clouds:** Which public clouds (AWS, Azure, GCP) to show the partner tile in.
- **Supported SQL Warehouses:** Which SKUs of SQL Warehouses (Classic, Pro, Serverless) to enable for the partner tile.
- **Category:** Which Category to display the partner tile in on the Databricks page.
- **External Location:** The ability for users to select an External Location for use by partners that are able to write external tables to Databricks.

### How do credentials and PATs get rotated?
- Credential rotation is currently manual and unscheduled.  If needed, the partner will provide Databricks new credentials, Databricks will update our systems, then the partner will remove support for the old credentials.
- PAT rotation is currently manual.  Databricks provides a non-expiring PAT.

### Why does the integration use PATs instead of OAuth?
Databricks OAuth support is under development.

### How can the partner tell if a connection has been deleted on the Databricks side?
Databricks will make an API call to the partner when a connection is deleted on the Databricks side if the partner provides a delete-connection endpoint.  Otherwise, if a partner sees a Connect request with is_connection_established == false for a workspace where the partner has a connection configured, they can infer that the previous connection was deleted.

### What should happen if a connection is deleted on either the Databricks side or the Partner side and user tries to create a new connection?
Databricks and the partner may have different values for whether the connection is established. A user may delete the connection on either the Databricks-side or the partner-side causing this mismatch. Here&#39;s what should happen with the Connect API in each of the 4 cases.

1. If Databricks has no connection configured, it will send a payload with **is\_connection\_established** set to **false**.
    1. If the partner has no connection configured, they will configure the connection.
    2. If the partner has a connection configured, they will configure a new, separate connection. This can happen if a connection was previously deleted on the Databricks-side or if a connection was manually configured from the partner to Databricks outside of Partner Connect.
2. If Databricks has a connection configured, it will send a payload with **is\_connection\_established** set to **true**.
    1. If the partner has no connection configured, the partner responds with 404 connection\_not\_found so that Databricks can tell the user to take action.
    2. If the partner has a connection configured, no new connection needs to be configured.


### What should happen if a second user from the same Databricks Workspace reuses a connection set up by another user?
Note that the second user does not need to be an admin on the Databricks side.

Many partners redirect to a page that tells the user they must get invited to the partner workspace by an admin.  Some partners automatically add the user to the partner workspace.

### How should the partner display side-by-side connections to multiple Databricks workspaces?
For partners whose workspaces will contain multiple Databricks Partner Connect connections, patterns seen to name them differently have included user-input naming, the workspace-id, or an appending incrementing counter.  Workspaces do have a name, but that isn't provided by the Connect API today.  The Get-Workspace REST API may be an option.

### What are the implications of requiring users to verify their email address?
The user's flow will continue from the link in their email inbox.  That landing page should meet the usability requirements (pre-configured Databricks connector that's impossible to miss).

### What are the implications of NOT pre-creating users/connections as part of the Connect API request and instead waiting for the redirect_url?
Since the test suite in this repository only tests the Connect API, many cases lose test coverage.

### How can the partner test new changes to the Connect API after GA?
A supported solution for this is under development.  In the meantime, please reach out directly if you're hitting limitations.

### Anything to know about special characters?
Identifiers (like catalog and schema names) in Databricks may need to be escaped with backticks (`).  Read more [here](https://docs.databricks.com/sql/language-manual/sql-ref-identifiers.html).