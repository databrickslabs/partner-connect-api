package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import org.openapitools.client.model.{
  ConnectRequest,
  ConnectRequestEnums,
  UserInfo
}
import spray.json._

class JsonFormattersTest extends PartnerTestBase {
  val testUserInfo = UserInfo(
    email = "test@mail.com",
    first_name = "test-first-name",
    last_name = "test-last-name",
    databricks_user_id = 5845867166711048519L,
    databricks_organization_id = 4645065419173783088L,
    is_connection_established = false
  )

  val testRequestFull = ConnectRequest(
    user_info = testUserInfo,
    connection_id = Some("test-connection-id"),
    hostname = "test-hostname",
    port = 443,
    workspace_url = "https://test-workspace-url",
    http_path = Some("test-http-path"),
    jdbc_url = Some("jdbc://test-jdcc-url"),
    databricks_jdbc_url = Some("jdbc://test-databricks-jdbc-url"),
    workspace_id = 1L,
    demo = true,
    cloud_provider = ConnectRequestEnums.CloudProvider.Aws,
    cloud_provider_region = Some("test-cloud-provider-region"),
    is_free_trial = true,
    destination_location = Some("test-destination-location"),
    catalog_name = Some("test-catalog-name"),
    database_name = Some("test-database-name"),
    cluster_id = Some("test-cluster-id"),
    is_sql_endpoint = Some(true),
    is_sql_warehouse = Some(true),
    data_source_connector = Some("test-data-source-connector"),
    service_principal_id = Some("test-service-principal-id"),
    service_principal_oauth_secret =
      Some("test-service-principal-oauth-secret"),
    connection_scope = Some(ConnectRequestEnums.ConnectionScope.Workspace),
    oauth_u2m_app_id = Some("test-oauth-u2m-app-id")
  )

  val connectionRequestJson =
    """{"catalog_name":"test-catalog-name","cloud_provider":"aws","cloud_provider_region":"test-cloud-provider-region","cluster_id":"test-cluster-id","connection_id":"test-connection-id","connection_scope":"workspace","data_source_connector":"test-data-source-connector","database_name":"test-database-name","databricks_jdbc_url":"jdbc://test-databricks-jdbc-url","demo":true,"destination_location":"test-destination-location","hostname":"test-hostname","http_path":"test-http-path","is_free_trial":true,"is_sql_endpoint":true,"is_sql_warehouse":true,"jdbc_url":"jdbc://test-jdcc-url","oauth_u2m_app_id":"test-oauth-u2m-app-id","port":443,"service_principal_id":"test-service-principal-id","service_principal_oauth_secret":"test-service-principal-oauth-secret","user_info":{"databricks_organization_id":4645065419173783088,"databricks_user_id":5845867166711048519,"email":"test@mail.com","first_name":"test-first-name","is_connection_established":false,"last_name":"test-last-name"},"workspace_id":1,"workspace_url":"https://test-workspace-url"}"""

  test(
    "serialize and deserialize ConnectRequest: All the fields are provided"
  ) {
    val jsonStr = testRequestFull.toJson.toString
    assert(jsonStr === connectionRequestJson)
    val actualRequest = jsonStr.parseJson.convertTo[ConnectRequest]
    assert(actualRequest == testRequestFull)
  }

  test(
    "serialize and deserialize ConnectRequest: All the option fields are None"
  ) {
    val expectedRequest = testRequestFull.copy(
      connection_id = None,
      http_path = None,
      jdbc_url = None,
      databricks_jdbc_url = None,
      cloud_provider_region = None,
      destination_location = None,
      catalog_name = None,
      database_name = None,
      cluster_id = None,
      is_sql_endpoint = None,
      is_sql_warehouse = None,
      service_principal_id = None,
      service_principal_oauth_secret = None,
      connection_scope = None
    )
    val jsonStr = expectedRequest.toJson.toString
    val actualRequest = jsonStr.parseJson.convertTo[ConnectRequest]
    assert(actualRequest == expectedRequest)
  }
}
