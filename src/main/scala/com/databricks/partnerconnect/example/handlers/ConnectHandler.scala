package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import com.databricks.partnerconnect.example.service.{
  AccountService,
  ConnectionService
}
import com.typesafe.scalalogging.Logger
import org.openapitools.client.model.ConnectionEnums.{
  AccountStatus,
  RedirectValue,
  UserStatus
}
import org.openapitools.client.model.{ConnectRequest, Connection, PartnerConfig}

/** Handler for connect request.
  */
class ConnectHandler(
    accountService: AccountService,
    connectionService: ConnectionService,
    val config: PartnerConfig
) extends Handler[ConnectRequest] {

  val logger = Logger[ConnectHandler]

  override def handle(request: ConnectRequest): StandardRoute = {
    logger.info(s"Processing connect Request: ${request}")
    if (request.user_info.is_connection_established) {
      ConnectHelper.processExistingConnection(
        accountService,
        connectionService,
        request,
        config
      )
    } else {
      val email = request.user_info.email
      val domainName = getDomain(email)
      var account = accountService.getAccountByName(domainName)
      val accountStatus = if (account.isDefined && !request.demo) {
        AccountStatus.Active
      } else {
        account = Some(accountService.getOrCreateAccount(domainName))
        AccountStatus.`New`
      }
      val userStatus = if (account.get.userExists(email) && !request.demo) {
        UserStatus.Existing
      } else {
        accountService.addUser(account.get.id, request.user_info.email)
        UserStatus.`New`
      }
      val connection = connectionService.createConnection(
        account.get.id,
        workspaceId = request.workspace_id,
        cloudProvider = request.cloud_provider
      )
      complete(
        Connection(
          redirect_uri = s"${config.base_url}/trial",
          redirect_value = RedirectValue.CreateTrial,
          connection_id = Some(connection.id),
          user_status = userStatus,
          account_status = accountStatus,
          configured_resources = true
        )
      )
    }
  }

  private def getDomain(email: String): String = {
    email
      .split("@")
      .lastOption
      .getOrElse(throw new IllegalArgumentException(s"Invalid email ${email}"))
  }
}
