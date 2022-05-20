package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import org.openapitools.client.model.ConnectorEnums.{`Type` => ConnectorType}
import org.openapitools.client.model.PartnerConfigEnums.Category
import org.openapitools.client.model.{
  Connector,
  ConnectorResponse,
  PartnerConfig
}
import com.databricks.partnerconnect.example.formatters.JsonFormatters._

class GetConnectorHandler(val config: PartnerConfig) extends Handler[String] {
  val page1 = Seq(Connector("amazon_s3", "Amazon S3", ConnectorType.Target))
  val page2 = Seq(Connector("adobe_ads", "Adobe Ads", ConnectorType.Source))
  override def handle(paginationToken: String): StandardRoute = {
    if (config.category == Category.INGEST) {
      if (paginationToken.isEmpty) {
        complete(
          ConnectorResponse(page1, Some("1"))
        )
      } else {
        complete(
          ConnectorResponse(page2)
        )
      }
    } else {
      complete(StatusCodes.NotFound)
    }
  }
}
