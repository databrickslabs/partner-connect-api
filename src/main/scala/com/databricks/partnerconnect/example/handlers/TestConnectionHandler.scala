package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import com.databricks.partnerconnect.example.service.ConnectionService
import org.openapitools.client.model.TestResultEnums.Status
import org.openapitools.client.model.{
  ConnectionInfo,
  ConnectionTestResult,
  ErrorResponse,
  TestResult
}
import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason

class TestConnectionHandler(connectionService: ConnectionService)
    extends Handler[ConnectionInfo] {
  override def handle(connection: ConnectionInfo): StandardRoute = {
    val connectionInfo =
      connectionService.getConnection(connection.connection_id)
    if (connectionInfo.isEmpty) {
      complete(
        StatusCodes.NotFound,
        ErrorResponse(
          ErrorReason.ConnectionNotFound,
          None,
          Some("Connection not found")
        )
      )
    } else {
      complete(
        ConnectionTestResult(
          Seq(
            TestResult(
              "get-connection",
              Status.PASS,
              Some(s"Connection exists.")
            )
          )
        )
      )
    }
  }
}
