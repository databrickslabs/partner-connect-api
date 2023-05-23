# Authorization
After creating a self-testing partner, use the [Databricks Secrets CLI](https://docs.databricks.com/dev-tools/cli/secrets-cli.html)
to create a secret for the endpoint credential.
Alternatively, the [Secrets API 2.0](https://docs.databricks.com/dev-tools/api/latest/secrets.html) can be called directly.

The scope for the secret must be `partner-connect` and the key must be the ID of the self-testing partner.
See the [example](#authorization-example) for more details.

**WARNING: Read the [Non-admin Authorization](#non-admin-authorization) section before creating the scope**

The value of the secret must be the exact content of the expected Authorization header.
When no Authorization header is expected, the secret must still be created and can be empty in value.

## Non-admin authorization
If `NON_ADMIN` is in `supported_features` (i.e. non-admins should be able to create a connection with the self-testing partner),
ensure that all users are able to read secrets in the scope.

This can be done at scope creation time or post-scope creation through the appropriate access control rule.

e.g. For post-scope creation: `databricks secrets put-acl --scope partner-connect --principal users --permission READ`

Verify the access control rules using the command ```databricks secrets list-acls --scope partner-connect```

### Example list-acls output
```
Principal                 Permission
------------------------  ------------
users                     READ
xxx.yyy@databricks.com    MANAGE
```

**Note: Workspace admins have the permission to manage all secret scopes in the workspace**

## Authorization Example
First, create the scope for the secret if it does not already exist, setting access control rules if needed.

e.g.
`databricks secrets create-scope --scope partner-connect [--initial-manage-principal users]`

After creating the self-testing partner, the ID of the created self-testing partner is outputted.

If the ID of the self-testing partner is `01EDC6E7BD3A15B5B7E5542FC1873E44` and `Basic CREDENTIALVALUE` is the expected
Authorization header value, put `Basic CREDENTIALVALUE` as the value under key `01EDC6E7BD3A15B5B7E5542FC1873E44`.

e.g.
`databricks secrets put --scope partner-connect --key 01EDC6E7BD3A15B5B7E5542FC1873E44 --string-value "Basic CREDENTIALVALUE"`

or

`databricks secrets put --scope partner-connect --key 01EDC6E7BD3A15B5B7E5542FC1873E44`, putting the secret into the opened editor
