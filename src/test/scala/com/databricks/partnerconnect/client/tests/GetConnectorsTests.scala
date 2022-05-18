package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil
import com.databricks.partnerconnect.example.util.PartnerConfigUtil.{
  newEmail,
  nextLong
}
import org.openapitools.client.core.{ApiResponse, BasicCredentials}
import org.openapitools.client.model.ConnectionEnums.RedirectValue
import org.openapitools.client.model.ConnectorResponse
import org.openapitools.client.model.ConnectorEnums.{`Type` => ConnectorType}
import org.openapitools.client.model.PartnerConfigEnums.Category

class GetConnectorsTests extends PartnerTestBase {

  test(
    s"P900 Get connector list returns list of source and target datasources the partner supports. ${configName}"
  ) {
    assume(config.category == Category.INGEST)
    assert(config.endpoints.get_connectors_path.isDefined)
    val request = createConnectRequest(
      false,
      newEmail("user1"),
      nextLong(),
      nextLong()
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)
    val connectors = getConnectors()
    validateConnectors(connectors)
    // Call again if pagination token is returned.
    for {
      token <- connectors.content.pagination_token
    } validateConnectors(getConnectors(Some(token)))
  }

  private def validateConnectors(connectors: ApiResponse[ConnectorResponse]) = {
    assert(connectors.code == 200)
    assert(connectors.content.connectors.nonEmpty)
    connectors.content.connectors.foreach(c => {
      assert(c.name.nonEmpty)
      assert(c.identifier.nonEmpty)
      assert(
        c.`type` == ConnectorType.Source || c.`type` == ConnectorType.Target
      )
    })
  }

  def getConnectors(
      paginationToken: Option[String] = Some("")
  ): ApiResponse[ConnectorResponse] = {
    val request = datasourceApi.getConnectors(
      paginationToken = paginationToken,
      acceptLanguage = Some("en-us"),
      userAgent = "databricks",
      contentType = Some("application/json")
    )(
      BasicCredentials(
        PartnerConfigUtil.getBasicAuthUser(),
        PartnerConfigUtil.getBasicAuthPassword()
      )
    )
    executeRequest[ConnectorResponse](request)
  }
}
