package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import com.databricks.partnerconnect.example.service.{
  AccountInfo,
  AccountService,
  ConnectionService
}
import org.openapitools.client.model.ConnectionEnums.{
  AccountStatus,
  RedirectValue,
  UserStatus
}
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import org.openapitools.client.model.PartnerConfigEnums.NewUserAction.{
  AutoAdd,
  Error,
  Invite,
  NotFound
}
import org.openapitools.client.model.{
  ConnectRequest,
  Connection,
  ErrorResponse,
  PartnerConfig
}

object ConnectHelper {

  /* The partner has four options on how they can handle new users.
   *   - auto_add: Add new user automatically to existing account return 200
   *     OK. \- invite: return 200 OK with redirect url to invite the user to
   *     join existing account. \- not_found: return 404 with account not found
   *     error. \- error: Return 500 with generic error.
   */
  def newUserRouteMap(
      config: PartnerConfig,
      accountStatus: AccountStatus,
      redirectValue: Option[RedirectValue] = None
  ): StandardRoute = {
    config.new_user_action match {
      case AutoAdd =>
        complete(
          Connection(
            redirect_uri = s"${config.base_url}/signin",
            redirect_value = redirectValue.getOrElse(RedirectValue.SignIn),
            user_status = UserStatus.`New`,
            account_status = accountStatus,
            configured_resources = false
          )
        )
      case Invite =>
        complete(
          Connection(
            redirect_uri = s"${config.base_url}/join",
            redirect_value =
              redirectValue.getOrElse(RedirectValue.ContactAdmin),
            user_status = UserStatus.NotApplicable,
            account_status = accountStatus,
            configured_resources = false
          )
        )
      case NotFound =>
        complete(
          StatusCodes.NotFound,
          ErrorResponse(
            ErrorReason.AccountNotFound,
            None,
            Some("Account not found for user")
          )
        )
      case Error =>
        complete(
          StatusCodes.InternalServerError,
          ErrorResponse(
            ErrorReason.GeneralError,
            None,
            Some("Unable to handle new user")
          )
        )
    }
  }

  /** Handle the connect flow when is_connection_established = true. This flow
    * could be an existing user (email) or a new email the partner have not seen
    * before.
    */
  def processExistingConnection(
      accountService: AccountService,
      connectionService: ConnectionService,
      connectRequest: ConnectRequest,
      config: PartnerConfig
  ): StandardRoute = {
    val existingConnection =
      connectionService.getConnection(connectRequest.connection_id.get)
    if (existingConnection.isEmpty) {
      // Connection is not found.
      complete(
        StatusCodes.NotFound,
        ErrorResponse(
          ErrorReason.ConnectionNotFound,
          None,
          Some(s"Connection id ${connectRequest.connection_id.get}")
        )
      )
    } else if (config.require_manual_signup.getOrElse(false)) {
      // Simulate partner that would need a manual signup
      complete(
        StatusCodes.NotFound,
        ErrorResponse(
          ErrorReason.AccountNotFound,
          None,
          Some(s"User account not found. Requires signup.")
        )
      )
    } else {
      val existingAccount: Option[AccountInfo] =
        accountService.getAccount(existingConnection.get.accountId)
      val redirectValue =
        if (existingAccount.get.status == AccountStatus.Expired)
          Some(RedirectValue.PurchaseProduct)
        else None
      if (existingAccount.isEmpty) {
        // If account can't be found for existing connection, throw server error.
        complete(
          StatusCodes.InternalServerError,
          ErrorResponse(
            ErrorReason.GeneralError,
            Some("Account not found for connection."),
            Some(
              s"Invalid state. Account not found for connection id ${connectRequest.connection_id.get}"
            )
          )
        )
      } else if (
        existingAccount.get.userExists(connectRequest.user_info.email)
      ) {
        // User is part of the account.
        complete(
          Connection(
            redirect_uri = "http://localhost:8080/signin",
            redirect_value = redirectValue.getOrElse(RedirectValue.SignIn),
            user_status = UserStatus.Existing,
            account_status = existingAccount.get.status,
            configured_resources = false
          )
        )
      } else {
        // New user in existing account.
        if (config.new_user_action == AutoAdd) {
          accountService.addUser(
            existingAccount.get.id,
            connectRequest.user_info.email
          )
        }
        newUserRouteMap(config, existingAccount.get.status, redirectValue)
      }
    }
  }
}
