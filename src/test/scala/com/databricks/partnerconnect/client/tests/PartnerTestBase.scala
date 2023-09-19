package com.databricks.partnerconnect.client.tests

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import com.databricks.partnerconnect.example.util.PartnerConfigUtil
import org.json4s.jackson.JsonMethods.compact
import org.json4s.{DefaultFormats, Extraction, Formats}
import org.openapitools.client.api.EnumsSerializers
import org.openapitools.client.core._
import org.openapitools.client.model.ConnectRequestEnums.CloudProvider
import org.openapitools.client.model.ConnectionEnums.{
  AccountStatus,
  RedirectValue,
  UserStatus
}
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import org.openapitools.client.model.ResourceToProvisionEnums.ResourceType.SqlEndpoint
import org.openapitools.client.model._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import spray.json._

import java.net.URL
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class PartnerTestBase
    extends EnvironmentSetupSuite
    with Matchers
    with BeforeAndAfterAll {
  // TODO: Make this configurable.
  val timeout: FiniteDuration = 60 seconds
  implicit val formats: Formats =
    DefaultFormats ++ Serializers.all ++ EnumsSerializers.all

  private val defaultRedirectValues: Seq[RedirectValue] =
    Seq(
      RedirectValue.CreateTrial,
      RedirectValue.SignIn,
      RedirectValue.PurchaseProduct
    )
  private val userStatuses: Seq[String] =
    Seq(
      UserStatus.`New`.toString,
      UserStatus.Existing.toString,
      UserStatus.NotApplicable.toString
    )
  private val accountStatuses: Seq[String] =
    Seq(
      AccountStatus.Active.toString,
      AccountStatus.`New`.toString,
      AccountStatus.NotApplicable.toString,
      AccountStatus.Expired.toString
    )

  private val workspace: PartnerConfigTestWorkspaceDetail =
    config.test_workspace_detail.getOrElse(
      PartnerConfigTestWorkspaceDetail(
        "testworkspace.databricks.com",
        443,
        "jdbc:spark://testworkspace.databricks.com:443/default;transportMode=http;ssl=1;AuthMech=3;httpPath=/sql/1.0/endpoints/44a1419108002906;",
        "jdbc:databricks://testworkspace.databricks.com:443/default;httpPath=/sql/1.0/endpoints/44a1419108002906;",
        "/sql/1.0/endpoints/44a1419108002906",
        "44a1419108002906"
      )
    )

  def createConnectRequest(
      isConnectionEstablished: Boolean,
      email: String,
      workspaceId: Long,
      userId: Long,
      connectionId: Option[String] = None,
      userName: Option[String] = None,
      password: Option[String] = None,
      cloudProvider: Option[CloudProvider] = None,
      demo: Option[Boolean] = None
  ): ApiRequest[Connection] = {

    val requiresSqlEndpoint =
      config.resources_to_provision.exists(_.resource_type == SqlEndpoint)
    val userInfo = UserInfo(
      auth =
        if (isConnectionEstablished) None
        else Some(Auth(PartnerConfigUtil.getPAT())),
      email = email,
      first_name = "TestFirstName",
      last_name = "TestLastName",
      databricks_user_id = userId,
      databricks_organization_id = workspaceId,
      is_connection_established = isConnectionEstablished
    )
    val connectionRequest = ConnectRequest(
      user_info = userInfo,
      workspace_id = workspaceId,
      hostname = workspace.hostname,
      port = workspace.port,
      workspace_url = s"https://${workspace.hostname}",
      cluster_id = Some(workspace.cluster_id),
      demo = demo.getOrElse(false),
      cloud_provider = cloudProvider.getOrElse(CloudProvider.Aws),
      cloud_provider_region = Some("us-west-2"),
      is_free_trial = false,
      destination_location = None,
      catalog_name = None,
      database_name = None,
      connection_id = connectionId,
      // Optional params only required for sql warehouse.
      http_path =
        if (requiresSqlEndpoint) Some(workspace.http_path)
        else None,
      jdbc_url =
        if (requiresSqlEndpoint)
          Some(
            workspace.jdbc_url
          )
        else None,
      databricks_jdbc_url =
        if (requiresSqlEndpoint)
          Some(
            workspace.databricks_jdbc_url
          )
        else None,
      is_sql_endpoint = Some(requiresSqlEndpoint),
      is_sql_warehouse = Some(requiresSqlEndpoint),
      service_principal_id = Some("a2a25a05-3d59-4515-a73b-b8bc5ab79e31")
    )
    val request = connectionApi.connect(
      connectRequest = connectionRequest,
      acceptLanguage = Some("en-US"),
      userAgent = "databricks",
      contentType = Some("application/json")
    )(
      BasicCredentials(
        userName.getOrElse(PartnerConfigUtil.getBasicAuthUser()),
        password.getOrElse(PartnerConfigUtil.getBasicAuthPassword())
      )
    )
    request
  }

  def executeConnectRequest(
      request: ApiRequest[Connection]
  ): ApiResponse[Connection] = {
    executeRequest[Connection](request)
  }

  def executeRequest[T: Manifest](
      request: ApiRequest[T]
  ): ApiResponse[T] = {
    val bodyString = request.bodyParam
      .map(Extraction.decompose)
      .map(compact)
      .getOrElse("")
    logger.info(
      s"Sending Request to ${request.basePath + request.operationPath} " +
        s"QueryParam: ${request.queryParams} " +
        s"Header: ${request.headerParams} " +
        s"Body: ${bodyString}"
    )
    Await.result(invoker.execute[T](request), timeout)
  }

  /** Extract the error response from the string returned by the API. This is a
    * workaround since the ApiInvoker returns a string that looks like this.
    * "HttpEntity.Strict(application/json,{"error_reason": "unauthorized"})"
    * @param error
    * @return
    */
  def extractErrorResponse(error: String): Option[ErrorResponse] = {
    val str = error.parseJson
    val map = str.convertTo[Map[String, String]]
    Some(ErrorResponse(ErrorReason.withName(map("error_reason"))))
  }

  def isSuccessStatus(status: Int) = {
    status == 200 || status == 201
  }

  /** Validate the response for successful connection
    */
  def validateNewConnection(
      connectionResult: ApiResponse[Connection],
      expectedRedirectValue: Option[RedirectValue] = None
  ): Unit = {
    assert(isSuccessStatus(connectionResult.code))
    assert(connectionResult.content.configured_resources)
    assert(connectionResult.content.connection_id.isDefined)
    assert(connectionResult.content.connection_id.get.nonEmpty)
    assert(connectionResult.content.redirect_uri.nonEmpty)
    assert(userStatuses.contains(connectionResult.content.user_status.toString))
    assert(
      accountStatuses.contains(connectionResult.content.account_status.toString)
    )
    validateRedirectValue(
      expectedRedirectValue.map(Seq(_)).getOrElse(Seq.empty),
      connectionResult.content.redirect_value
    )
    validateRedirectUri(connectionResult.content.redirect_uri)
  }

  def signInAndValidate(
      request: ApiRequest[Connection],
      expectedRedirectValues: Seq[RedirectValue] = Seq()
  ): Option[ApiResponse[Connection]] = {
    if (config.require_manual_signup.getOrElse(false)) {
      // if manual signup is required connect should throw 404 account_not_found
      val error = intercept[ApiError[String]] {
        executeConnectRequest(request)
      }
      validateError(
        error,
        404,
        Seq(ErrorReason.AccountNotFound, ErrorReason.ConnectionNotFound)
      )
      None
    } else {
      val signInResult = executeConnectRequest(request)
      validateSignIn(signInResult)
      validateRedirectValue(
        expectedRedirectValues,
        signInResult.content.redirect_value
      )
      Some(signInResult)
    }
  }

  private def validateSignIn(
      connectionResult: ApiResponse[Connection]
  ): Unit = {
    assert(connectionResult.code == 200)
    assert(!connectionResult.content.configured_resources)
    // Connection id should not be returned for existing connection.
    assert(connectionResult.content.connection_id.isEmpty)
    assert(connectionResult.content.redirect_uri.nonEmpty)
    assert(
      userStatuses.contains(connectionResult.content.user_status.toString)
    )
    assert(
      accountStatuses.contains(
        connectionResult.content.account_status.toString
      )
    )
    validateRedirectUri(connectionResult.content.redirect_uri)
  }

  private def validateRedirectValue(
      expectedRedirectValues: Seq[RedirectValue],
      actualRedirect: RedirectValue
  ): Unit = {
    if (expectedRedirectValues.nonEmpty) {
      assert(
        expectedRedirectValues.contains(actualRedirect)
      )
    } else {
      // If not specified redirect value should be sign in or create trial in successful connections.
      assert(
        defaultRedirectValues.contains(actualRedirect)
      )
    }
  }

  def validateRedirectUri(uri: String): Unit = {
    val response: HttpResponse =
      Await.result(Http().singleRequest(HttpRequest(uri = Uri(uri))), timeout)
    assert(
      response.status.isSuccess() || response.status.isRedirection(),
      s"Invalid return code ${response.status} for redirect_uri: ${uri}"
    )

    val url = new URL(uri)
    val urlHost = url.getHost
    assert(
      config.hostnames.exists(hostname => urlHost.contains(hostname)),
      s"Hostname [$urlHost] not found in configured validHostnames.  Share all expected hostnames with Databricks for allow-listing."
    )
  }

  def validateError(
      caught: ApiError[String],
      expectedCode: Int,
      expectedErrorReason: ErrorReason
  ): Unit = {
    validateError(caught, expectedCode, Seq(expectedErrorReason))
  }

  def validateError(
      caught: ApiError[String],
      expectedCode: Int,
      expectedErrorReasons: Seq[ErrorReason]
  ): Unit = {
    assert(
      caught.code == expectedCode,
      s"Unexpected status code. Error: ${caught}"
    )
    assert(caught.responseContent.isDefined, "Response body is required.")
    val error = extractErrorResponse(caught.responseContent.get)
    assert(
      error.isDefined,
      s"Invalid error response: ${caught.responseContent.get}"
    )
    assert(
      expectedErrorReasons.contains(error.get.error_reason),
      "Invalid error reason."
    )
  }

  def deleteConnection(
      id: String,
      cloudProvider: CloudProvider,
      workspaceId: Long
  ): ApiResponse[DeleteConnectionResponse] = {
    val request = connectionApi.deleteConnection(
      deleteConnectionRequest =
        DeleteConnectionRequest(id, cloudProvider.toString, workspaceId),
      acceptLanguage = Some("en-US"),
      userAgent = "databricks",
      contentType = Some("application/json")
    )(
      BasicCredentials(
        PartnerConfigUtil.getBasicAuthUser(),
        PartnerConfigUtil.getBasicAuthPassword()
      )
    )
    executeRequest[DeleteConnectionResponse](request)
  }

  def deleteAccount(
      domain: String,
      cloudProvider: CloudProvider,
      workspaceId: Long
  ): ApiResponse[Unit] = {
    val request = accountApi.deleteAccount(
      accountInfo = AccountInfo(domain, cloudProvider.toString, workspaceId),
      acceptLanguage = Some("en-US"),
      userAgent = "databricks",
      contentType = Some("application/json")
    )(
      BasicCredentials(
        PartnerConfigUtil.getBasicAuthUser(),
        PartnerConfigUtil.getBasicAuthPassword()
      )
    )
    executeRequest[Unit](request)
  }
}
