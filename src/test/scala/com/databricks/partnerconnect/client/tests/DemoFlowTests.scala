package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil.{
  newDomain,
  nextLong
}
import org.openapitools.client.core.ApiResponse
import org.openapitools.client.model.Connection
import org.openapitools.client.model.ConnectionEnums.{
  AccountStatus,
  RedirectValue,
  UserStatus
}

class DemoFlowTests extends PartnerTestBase {

  test(
    "P700 When a connect api is called with the demo flag, the partner redirects user to the trial flow."
  ) {
    assume(config.supports_demo.isDefined)
    // Now add multiple demo accounts.
    val domain = newDomain()
    val workspace = nextLong()
    testDemo(workspace, nextLong(), s"user1@$domain")
    testDemo(workspace, nextLong(), s"user2@$domain")
  }

  private def testDemo(
      workspaceId: Long,
      userId: Long,
      email: String
  ): Unit = {
    val demoRequest = createConnectRequest(
      false,
      email,
      workspaceId,
      userId,
      demo = Some(true)
    )
    val demoResponse = executeConnectRequest(demoRequest)
    // Expect trial since its a new demo flow even if the workspace is not new.
    validateNewConnection(demoResponse, Some(RedirectValue.CreateTrial))
    assert(UserStatus.`New` === demoResponse.content.user_status)
    assert(
      AccountStatus.`New`.toString === demoResponse.content.account_status.toString
    )
    // Validate signing in with demo account.
    val signIn = createConnectRequest(
      true,
      email,
      workspaceId,
      userId,
      connectionId = demoResponse.content.connection_id
    )
    val signInResult = executeConnectRequest(signIn)
    validateSignIn(signInResult)
  }
}
