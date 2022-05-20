package com.databricks.partnerconnect.example

import akka.http.scaladsl.model.{
  ContentTypes,
  HttpEntity,
  HttpResponse,
  StatusCodes
}
import akka.http.scaladsl.server.RejectionHandler
import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import org.openapitools.client.model.ErrorResponse
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import spray.json.enrichAny

object PartnerRejectionHandler {
  def rejectionHandler =
    RejectionHandler.default
      .mapRejectionResponse {
        case res @ HttpResponse(
              StatusCodes.BadRequest,
              _,
              ent: HttpEntity.Strict,
              _
            ) =>
          res.withEntity(
            HttpEntity(
              ContentTypes.`application/json`,
              ErrorResponse(
                ErrorReason.BadRequest,
                None,
                Some(s"${ent.data.utf8String}")
              ).toJson.compactPrint
            )
          )
        // 401 | 403
        case res @ (HttpResponse(
              StatusCodes.Unauthorized,
              _,
              _,
              _
            ) | HttpResponse(
              StatusCodes.Forbidden,
              _,
              _,
              _
            )) =>
          res.withEntity(
            HttpEntity(
              ContentTypes.`application/json`,
              ErrorResponse(
                ErrorReason.Unauthorized,
                None,
                None
              ).toJson.compactPrint
            )
          )
        // 500
        case res @ HttpResponse(
              StatusCodes.InternalServerError,
              _,
              _,
              _
            ) =>
          res.withEntity(
            HttpEntity(
              ContentTypes.`application/json`,
              ErrorResponse(
                ErrorReason.GeneralError,
                None,
                None
              ).toJson.compactPrint
            )
          )
        case x => x
      }
}
