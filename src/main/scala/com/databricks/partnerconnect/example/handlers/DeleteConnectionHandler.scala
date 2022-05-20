package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import com.databricks.partnerconnect.example.service.ConnectionService
import org.openapitools.client.model.{ConnectionInfo, ErrorResponse}
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import com.databricks.partnerconnect.example.formatters.JsonFormatters._

class DeleteConnectionHandler(connectionService: ConnectionService)
    extends Handler[ConnectionInfo] {
  override def handle(connection: ConnectionInfo): StandardRoute = {
    if (connectionService.getConnection(connection.connection_id).isDefined) {
      connectionService.deleteConnection(connection.connection_id)
      complete(StatusCodes.OK, HttpEntity.Empty)
    } else {
      complete(
        StatusCodes.NotFound,
        ErrorResponse(
          ErrorReason.ConnectionNotFound,
          None,
          Some("Connection not found")
        )
      )
    }
  }
}
