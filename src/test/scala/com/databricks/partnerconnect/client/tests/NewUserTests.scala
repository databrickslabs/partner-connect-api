package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil.{
  newDomain,
  newEmail,
  nextLong
}
import org.openapitools.client.core.ApiError
import org.openapitools.client.model.ConnectRequestEnums.CloudProvider
import org.openapitools.client.model.ConnectionEnums.{RedirectValue, UserStatus}
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import org.openapitools.client.model.PartnerConfigEnums.NewUserAction
import org.scalatest.BeforeAndAfterEach

class NewUserTests extends PartnerTestBase with BeforeAndAfterEach {
  val successMap: Map[NewUserAction, (UserStatus, Seq[RedirectValue])] =
    Map(
      NewUserAction.AutoAdd -> (UserStatus.`New`, Seq(
        RedirectValue.SignIn,
        RedirectValue.CreateTrial
      )),
      NewUserAction.Invite -> (UserStatus.NotApplicable, Seq(
        RedirectValue.ContactAdmin
      ))
    )

  val errorMap: Map[NewUserAction, (Int, ErrorReason)] = Map(
    NewUserAction.NotFound -> (404, ErrorReason.AccountNotFound),
    NewUserAction.Error -> (500, ErrorReason.GeneralError)
  )

  val workspaceId = nextLong()

  // Generate success cases.
  successMap.keys.foreach(x => {
    test(
      s"P302 Second user NOT in partner account is able to sign in with existing PC connection(Blue check) (Join scenario) - success - ${x} - ${configName}"
    ) {
      assume(config.new_user_action == x)

      val domain = newDomain()
      val request = createConnectRequest(
        isConnectionEstablished = false,
        email = s"user1@${domain}",
        workspaceId = workspaceId,
        userId = nextLong()
      )
      val connectResult = executeConnectRequest(request)
      validateNewConnection(connectResult)

      val signIn = createConnectRequest(
        isConnectionEstablished = true,
        email = s"user2@${domain}",
        workspaceId = workspaceId,
        userId = nextLong(),
        connectionId = connectResult.content.connection_id
      )
      // Sign in second user.
      val expected = successMap(x)
      val signInResult = executeConnectRequest(signIn)
      assert(!signInResult.content.configured_resources)

      assert(signInResult.content.user_status.toString == expected._1.toString)
      assert(
        expected._2.contains(signInResult.content.redirect_value),
        s"Invalid redirect_value for redirect_uri: ${signInResult.content.redirect_uri}"
      )
    }
  })

  // Generate tests for failure case.
  errorMap.keys.foreach(x => {
    test(
      s"P302 Second user NOT in partner account is able to sign in with existing PC connection(Blue check) (Join scenario) - failure - ${x} - ${configName}"
    ) {
      assume(config.new_user_action == x)
      val domain = newDomain()
      val request = createConnectRequest(
        false,
        email = s"user1@${domain}",
        workspaceId = workspaceId,
        userId = nextLong()
      )
      val connectResult = executeConnectRequest(request)
      validateNewConnection(connectResult)

      val signIn = createConnectRequest(
        isConnectionEstablished = true,
        email = s"user2@${domain}",
        workspaceId = workspaceId,
        userId = nextLong(),
        connectionId = connectResult.content.connection_id
      )

      // Sign in second user and validate error.
      val expected = errorMap(x)
      val signInResult = intercept[ApiError[String]] {
        executeConnectRequest(signIn)
      }
      assert(signInResult.code == expected._1)
      val error = extractErrorResponse(signInResult.responseContent.get)
      assert(
        error.isDefined,
        s"Invalid error response: ${signInResult.responseContent.get}"
      )
      assert(
        error.get.error_reason == expected._2,
        "Invalid error reason."
      )
    }
  })
}
