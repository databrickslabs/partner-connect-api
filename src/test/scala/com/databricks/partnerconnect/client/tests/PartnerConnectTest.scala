package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil.{
  newEmail,
  nextLong
}
import org.openapitools.client.model.ConnectionEnums.RedirectValue
import org.openapitools.client.model.PartnerConfigEnums.NewUserAction

class PartnerConnectTest extends PartnerTestBase {

  test(
    s"P100 - New user/workspace is able to create connection - ${configName}"
  ) {
    val workspaceId = nextLong()
    val userId = nextLong()
    val request = createConnectRequest(
      false,
      newEmail("user1"),
      workspaceId,
      userId
    )
    val res = executeConnectRequest(request)
    // Validate we get the create trial flow for new user/workspace
    validateNewConnection(res, Some(RedirectValue.CreateTrial))
  }

  test(
    s"P300 - Existing admin user is able to sign in with existing PC connection(Blue check) - ${configName}"
  ) {
    val workspaceId = nextLong()
    val userId = nextLong()
    val email = newEmail("user1")
    val request = createConnectRequest(
      false,
      email,
      workspaceId,
      userId
    )
    val connectResult = executeConnectRequest(request)
    validateNewConnection(connectResult, Some(RedirectValue.CreateTrial))

    // Sign in
    val signIn = createConnectRequest(
      true,
      email,
      workspaceId,
      userId,
      connectionId = connectResult.content.connection_id
    )
    signInAndValidate(signIn)
  }
}
