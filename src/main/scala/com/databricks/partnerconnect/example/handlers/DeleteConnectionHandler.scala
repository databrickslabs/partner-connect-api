package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import com.databricks.partnerconnect.example.service.ConnectionService
import org.openapitools.client.model.DeleteConnectionResponseEnums.ResourceStatus
import org.openapitools.client.model.{
  DeleteConnectionRequest,
  ErrorResponse,
  DeleteConnectionResponse
}
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import com.databricks.partnerconnect.example.formatters.JsonFormatters._

class DeleteConnectionHandler(connectionService: ConnectionService)
    extends Handler[DeleteConnectionRequest] {
  override def handle(connection: DeleteConnectionRequest): StandardRoute = {
    if (connectionService.getConnection(connection.connection_id).isDefined) {
      connectionService.deleteConnection(connection.connection_id)
      complete(
        StatusCodes.OK,
        DeleteConnectionResponse(resource_status =
          ResourceStatus.ResourcesDeleted
        )
      )
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
