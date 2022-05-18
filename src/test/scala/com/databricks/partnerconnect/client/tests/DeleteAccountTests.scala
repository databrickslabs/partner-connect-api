package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil.{
  newEmail,
  nextLong
}
import org.openapitools.client.core.{ApiError, ApiResponse, BasicCredentials}
import org.openapitools.client.model.AccountInfo
import org.openapitools.client.model.ConnectionEnums.RedirectValue
import org.openapitools.client.model.ConnectRequestEnums.CloudProvider
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason

class DeleteAccountTests extends PartnerTestBase {
  test(
    s"P403 - Admin is able to re-create connection after deleting existing PC connection and partner account (reset to new). - ${configName}"
  ) {
    assume(config.endpoints.delete_account_path.isDefined)
    // Create a new connection.
    val workspaceId = nextLong()
    val cloudProvider = CloudProvider.Azure
    val request = createConnectRequest(
      false,
      "user1@databricks-test.com",
      workspaceId,
      nextLong(),
      cloudProvider = Some(cloudProvider)
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)

    // Delete Account
    deleteAccount(
      "databricks-test.com",
      cloudProvider,
      workspaceId
    )
    // Create a new connection.
    val newConnection = executeConnectRequest(request)
    validateNewConnection(newConnection)
  }

  test(
    s"P405 Calling delete-account api with non demo/test account throws 400 bad request error. - ${configName}"
  ) {
    assume(config.endpoints.delete_account_path.isDefined)
    // Create a new connection.
    val workspaceId = nextLong()
    val cloudProvider = CloudProvider.Azure
    val request = createConnectRequest(
      false,
      "user1@realuseraccount.com",
      workspaceId,
      nextLong(),
      cloudProvider = Some(cloudProvider)
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)

    // Delete Account
    val caught = intercept[ApiError[String]] {
      deleteAccount(
        "realuseraccount.com",
        cloudProvider,
        workspaceId
      )
    }
    validateError(caught, 400, ErrorReason.BadRequest)
  }

  test(
    s"P404 Sign in shows account error (connection_not_found) if the partner account is deleted in the partner product. - ${configName}"
  ) {
    assume(config.endpoints.delete_account_path.isDefined)
    // Create a new connection.
    val workspaceId = nextLong()
    val userId = nextLong()
    val email = "user2@databricks-test.com"
    val cloudProvider = CloudProvider.Azure
    val request = createConnectRequest(
      false,
      email,
      workspaceId,
      userId,
      cloudProvider = Some(cloudProvider)
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)

    // Delete Account
    deleteAccount(
      "databricks-test.com",
      cloudProvider,
      workspaceId
    )

    // Sign in with deleted account
    val signIn = createConnectRequest(
      true,
      email,
      workspaceId,
      userId,
      connectionId = res.content.connection_id
    )
    val caught = intercept[ApiError[String]] {
      executeConnectRequest(signIn)
    }
    validateError(caught, 404, ErrorReason.ConnectionNotFound)
  }
}
