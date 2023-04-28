package com.databricks.partnerconnect.client.tests

import org.openapitools.client.core.{ApiError, ApiResponse, BasicCredentials}
import org.openapitools.client.model.ConnectionEnums.RedirectValue
import org.openapitools.client.model.ConnectionInfo
import org.openapitools.client.model.ConnectRequestEnums.CloudProvider
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import com.databricks.partnerconnect.example.util.PartnerConfigUtil.{
  newEmail,
  nextLong
}

class DeleteConnectionTests extends PartnerTestBase {

  test(
    s"P401 Admin is able to re-create connection after deleting existing PC connection and partner connection ${configName}"
  ) {
    assume(config.endpoints.delete_connection_path.isDefined)

    val cloudProvider = CloudProvider.Aws
    val workspaceId = nextLong()
    val userId = nextLong()
    // Create a new connection.
    val request = createConnectRequest(
      isConnectionEstablished = false,
      email = newEmail("user1"),
      workspaceId = workspaceId,
      userId = userId
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)

    // Delete connection
    deleteConnection(res.content.connection_id.get, cloudProvider, workspaceId)
    // Create a new connection.
    val newConnection = executeConnectRequest(request)
    validateNewConnection(newConnection)
  }

  test(
    s"P400 Admin is able to re-create connection after deleting existing PC connection. - ${configName}"
  ) {
    // Create a new connection.
    val request = createConnectRequest(
      false,
      newEmail("user2"),
      nextLong(),
      nextLong()
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)

    // Create a new connection with the same user and workspace to simulate deletion of partner connect connection.
    val newConnection = executeConnectRequest(request)
    validateNewConnection(newConnection)
  }

  test(
    s"P402 Sign in shows correct error (connection not found) if the partner connection is deleted in the partner product. ${configName}"
  ) {
    assume(config.endpoints.delete_connection_path.isDefined)
    // Create a new connection.
    val userId = nextLong()
    val workspaceId = nextLong()
    val cloudProvider = CloudProvider.Aws
    val email = newEmail("user3")
    val request = createConnectRequest(
      false,
      email,
      workspaceId,
      userId
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)

    // Delete connection from the partner side.
    deleteConnection(res.content.connection_id.get, cloudProvider, workspaceId)

    // Sign in with deleted connection.
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
