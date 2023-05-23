# Self-Testing Partner APIs
There is a set of four APIs for CRUD operations on self-testing partners.

## Authentication
Follow the [Databricks Authentication docs](https://docs.databricks.com/dev-tools/api/latest/authentication.html) to
set up authentication with the self-testing partner APIs.
The use of personal access tokens are recommended over username and password.

## Usage
After creating the self-testing partner, [set up the
Authorization header](Authorization.md) that Partner Connect will use with your endpoints.

## Create
### Endpoint
`https://<instance-name>.cloud.databricks.com/api/2.0/partnerhub/self-test-partners/create/`

### Request Body
Create expects a [partner configuration](README.md#config-specifications) in JSON format.

### Response
The ID of the created self-testing partner will be returned on success.

Ensure that [authorization](Authorization.md) is set up before using the self-testing partner.

## Get
### Endpoint
`https://<instance-name>.cloud.databricks.com/api/2.0/partnerhub/self-test-partners/get/`

### Request Body
The request body is empty.

### Response
The names and IDs of active self testing partners will be returned. 
Currently, there can only be one self-testing partner per workspace.
```
{
  "partners": [
    {
      "id": "363E6E44B000020",
      "name": "Self-Testing Partner"
    }
  ]
}
```

## Update
### Endpoint
`https://<instance-name>.cloud.databricks.com/api/2.0/partnerhub/self-test-partners/update/[id]`

The call to the endpoint must include the ID of the self-testing partner to be updated.

For instance, call `https://<instance-name>.cloud.databricks.com/api/2.0/partnerhub/self-test-partners/update/363E6E44B000020` 
if the ID is `363E6E44B000020`.

### Request Body
Update expects a [partner configuration](README.md#config-specifications) in JSON format.

### Response
Empty on success.

## Delete
### Endpoint
`https://<instance-name>.cloud.databricks.com/api/2.0/partnerhub/self-test-partners/delete/[id]`

The call to the endpoint must include the ID of the self-testing partner to be deleted.

For instance, call `https://<instance-name>.cloud.databricks.com/api/2.0/partnerhub/self-test-partners/delete/363E6E44B000020`
if the ID is `363E6E44B000020`.

### Request Body
The request body is empty.

### Response
Empty on success.
