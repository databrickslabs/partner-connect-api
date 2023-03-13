package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil
import com.databricks.partnerconnect.example.util.PartnerConfigUtil.{
  newEmail,
  nextLong
}
import org.openapitools.client.core.{ApiError, ApiResponse, BasicCredentials}
import org.openapitools.client.model.ConnectionEnums.RedirectValue
import org.openapitools.client.model.ConnectRequestEnums.CloudProvider
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import org.openapitools.client.model.TestResultEnums.Status
import org.openapitools.client.model.{ConnectionInfo, ConnectionTestResult}

class TestConnectionApiTests extends PartnerTestBase {

  test(
    s"P800 Test connection api returns valid result after testing a PC provisioned connection.  ${configName}"
  ) {
    assume(config.endpoints.test_connection_path.isDefined)
    val request = createConnectRequest(
      false,
      email = newEmail("user1"),
      workspaceId = nextLong(),
      userId = nextLong()
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)
    val testResult = testConnection(res.content.connection_id.get)
    assert(200 == testResult.code)
//    assert(testResult.content.test_results.nonEmpty)
    testResult.content.test_results.foreach(r => {
      assert(r.status == Status.PASS || r.status == Status.FAIL)
      assert(r.test_name.nonEmpty)
      assert(r.message.isDefined)
    })
  }

  test(
    s"P801 Test connection api returns failing test result if the connection on the partner side is deleted. ${configName}"
  ) {
    assume(config.endpoints.test_connection_path.isDefined)
    assume(config.endpoints.delete_connection_path.isDefined)

    val cloudProvider = CloudProvider.Aws
    val workspaceId = nextLong()
    val request = createConnectRequest(
      false,
      email = newEmail("user1"),
      workspaceId = workspaceId,
      userId = nextLong()
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)
    // Delete connection
    deleteConnection(res.content.connection_id.get, cloudProvider, workspaceId)
    // Validate test result
    val result = intercept[ApiError[String]] {
      testConnection(res.content.connection_id.get)
    }
    assert(result.code == 404)
    validateError(result, 404, ErrorReason.ConnectionNotFound)
  }

  def testConnection(
      connectionId: String
  ): ApiResponse[ConnectionTestResult] = {
    val request = connectionApi.testConnection(
      connectionInfo = ConnectionInfo(connectionId),
      acceptLanguage = Some("en-us"),
      userAgent = "databricks",
      contentType = Some("application/json")
    )(
      BasicCredentials(
        PartnerConfigUtil.getBasicAuthUser(),
        PartnerConfigUtil.getBasicAuthPassword()
      )
    )
    executeRequest[ConnectionTestResult](request)
  }
}
