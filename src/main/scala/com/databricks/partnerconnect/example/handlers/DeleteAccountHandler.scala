package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import com.databricks.partnerconnect.example.service.{
  AccountService,
  ConnectionService
}
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import org.openapitools.client.model.{AccountInfo, ErrorResponse}
import com.databricks.partnerconnect.example.formatters.JsonFormatters._

class DeleteAccountHandler(
    accountService: AccountService,
    connectionService: ConnectionService
) extends Handler[AccountInfo] {
  override def handle(account: AccountInfo): StandardRoute = {
    val result = accountService.getAccountByName(account.domain)
    if (result.isDefined) {
      // Delete connections and account.
      connectionService.deleteConnections(result.get.id)
      accountService.deleteAccount(result.get.id)
      complete(StatusCodes.OK, HttpEntity.Empty)
    } else {
      complete(
        StatusCodes.NotFound,
        ErrorResponse(
          ErrorReason.AccountNotFound,
          None,
          Some("Account not found")
        )
      )
    }
  }
}
