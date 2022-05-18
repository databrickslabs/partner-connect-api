package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil
import com.databricks.partnerconnect.example.util.PartnerConfigUtil.nextLong
import org.openapitools.client.core.{ApiResponse, BasicCredentials}
import org.openapitools.client.model.{AccountUserInfo, Connection}
import org.openapitools.client.model.ConnectRequestEnums.CloudProvider
import org.openapitools.client.model.ConnectionEnums.{
  AccountStatus,
  RedirectValue
}
import org.openapitools.client.model.PartnerConfigEnums.TrialType

class ExpireAccountTests extends PartnerTestBase {

  // Map to track the expected account status for a trial type.
  private val secondUserStatusMap: Map[TrialType, AccountStatus] = Map(
    TrialType.User -> AccountStatus.Active,
    TrialType.Account -> AccountStatus.Expired
  )

  test(
    "P501 Clicking blue checkmark tile, when the trial in the partner product is expired, leads to expected behavior"
  ) {
    assume(config.endpoints.expire_account_path.isDefined)
    // Create a new connection.
    val workspaceId = nextLong()
    val userId = nextLong()
    val email = "user1@databricks-test.com"
    createExpiredConnection(email, workspaceId, userId)
  }

  test(
    "P502 As second user with no partner account, clicking blue checkmark tile, when the trial in the partner product is expired, leads to expected behavior"
  ) {
    assume(config.endpoints.expire_account_path.isDefined)
    assume(config.trial_type.isDefined)
    // Create a new connection.
    val workspaceId = nextLong()
    val userId1 = nextLong()
    val email1 = "user1@databricks-test.com"
    val connection1 = createExpiredConnection(email1, workspaceId, userId1)
    // Sign in with second user
    val userId2 = nextLong()
    val email2 = "user2@databricks-test.com"
    val signIn = createConnectRequest(
      true,
      email2,
      workspaceId,
      userId2,
      connectionId = connection1.connection_id
    )
    val signInResult = executeConnectRequest(signIn)
    validateSignIn(signInResult)
    // If trial account is at the user level, we expect active account.
    // If it is at the account level, we expect the account_status to be in expired state.
    assert(
      signInResult.content.account_status == secondUserStatusMap(
        config.trial_type.get
      )
    )
  }

  private def createExpiredConnection(
      email: String,
      workspaceId: Long,
      userId: Long
  ): Connection = {
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

    // Expire Account
    expireAccount(email, userId, workspaceId, cloudProvider)
    val signIn = createConnectRequest(
      true,
      email,
      workspaceId,
      userId,
      connectionId = res.content.connection_id
    )
    val signInResult = executeConnectRequest(signIn)
    validateSignIn(signInResult, Some(RedirectValue.PurchaseProduct))
    assert(signInResult.content.account_status == AccountStatus.Expired)
    res.content
  }

  def expireAccount(
      email: String,
      userId: Long,
      workspaceId: Long,
      cloudProvider: CloudProvider
  ): ApiResponse[Unit] = {
    val request = accountApi.expireAccount(
      accountUserInfo =
        AccountUserInfo(email, userId, cloudProvider.toString, workspaceId),
      acceptLanguage = Some("en-US"),
      userAgent = "databricks",
      contentType = Some("application/json")
    )(
      BasicCredentials(
        PartnerConfigUtil.getBasicAuthUser(),
        PartnerConfigUtil.getBasicAuthPassword()
      )
    )
    executeRequest[Unit](request)
  }
}
