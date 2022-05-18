package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import com.databricks.partnerconnect.example.service.{AccountService}
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import org.openapitools.client.model.{AccountUserInfo, ErrorResponse}

class ExpireAccountHandler(
    accountService: AccountService
) extends Handler[AccountUserInfo] {
  override def handle(account: AccountUserInfo): StandardRoute = {
    val result = accountService.getAccountByEmail(account.email)
    result match {
      case Some(a) =>
        accountService.expireAccount(a.id)
        complete(StatusCodes.OK, HttpEntity.Empty)
      case None =>
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
