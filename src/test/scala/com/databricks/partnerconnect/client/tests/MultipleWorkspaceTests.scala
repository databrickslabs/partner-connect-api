package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil.{
  newEmail,
  nextLong
}
import org.openapitools.client.model.ConnectionEnums.RedirectValue

class MultipleWorkspaceTests extends PartnerTestBase {

  test(
    s"P600 - One admin user is able to create new connections in multiple workspaces. - ${configName}"
  ) {
    val userId = nextLong()
    val email = newEmail("user1")
    testConnection(nextLong(), userId, email)
    testConnection(nextLong(), userId, email)
  }

  private def testConnection(
      workspaceId: Long,
      userId: Long,
      email: String
  ): Unit = {

    val request = createConnectRequest(
      false,
      email,
      workspaceId,
      userId
    )
    val connectResult = executeConnectRequest(request)
    validateNewConnection(connectResult)

    // Sign in
    val signIn = createConnectRequest(
      true,
      email,
      workspaceId,
      userId,
      connectionId = connectResult.content.connection_id
    )
    val signInResult = executeConnectRequest(signIn)
    validateSignIn(signInResult)
  }
}
