package com.databricks.partnerconnect.example.formatters

import org.openapitools.client.model.ConnectRequestEnums.{
  CloudProvider,
  ConnectionScope
}
import org.openapitools.client.model.ConnectionEnums.{
  AccountStatus,
  RedirectValue,
  UserStatus
}
import org.openapitools.client.model.ConnectorEnums.{`Type` => ConnectorType}
import org.openapitools.client.model.DeleteConnectionResponseEnums.ResourceStatus
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import org.openapitools.client.model.PartnerConfigEnums.{
  Category,
  NewUserAction,
  TrialType
}
import org.openapitools.client.model.ResourceToProvisionEnums.ResourceType
import org.openapitools.client.model.TestResultEnums.Status
import org.openapitools.client.model.{Connector, _}
import spray.json._

object JsonFormatters extends DefaultJsonProtocol {
  // Order of declaration matters. Enums need to be defined first otherwise ProductFormats.scala throws NPE.
  implicit val errorReason: EnumJsonFormatter[ErrorReason.type] =
    new EnumJsonFormatter(ErrorReason)
  implicit val cloudProvider: EnumJsonFormatter[CloudProvider.type] =
    new EnumJsonFormatter(CloudProvider)
  implicit val connectionScope: EnumJsonFormatter[ConnectionScope.type] =
    new EnumJsonFormatter(ConnectionScope)
  implicit val userStatus: EnumJsonFormatter[UserStatus.type] =
    new EnumJsonFormatter(UserStatus)
  implicit val accountStatus: EnumJsonFormatter[AccountStatus.type] =
    new EnumJsonFormatter(AccountStatus)
  implicit val redirectValue: EnumJsonFormatter[RedirectValue.type] =
    new EnumJsonFormatter(RedirectValue)
  implicit val category: EnumJsonFormatter[Category.type] =
    new EnumJsonFormatter(Category)
  implicit val newUserAction: EnumJsonFormatter[NewUserAction.type] =
    new EnumJsonFormatter(NewUserAction)
  implicit val testStatus: EnumJsonFormatter[Status.type] = {
    new EnumJsonFormatter(Status)
  }
  implicit val connectorType: EnumJsonFormatter[ConnectorType.type] = {
    new EnumJsonFormatter(ConnectorType)
  }
  implicit val resourceStatus: EnumJsonFormatter[ResourceStatus.type] =
    new EnumJsonFormatter(ResourceStatus)

  implicit val resourceType: EnumJsonFormatter[ResourceType.type] =
    new EnumJsonFormatter(ResourceType)
  implicit val permissionLevel
      : EnumJsonFormatter[ObjectPermissionEnums.Level.type] = {
    new EnumJsonFormatter(ObjectPermissionEnums.Level)
  }
  implicit val permissionName
      : EnumJsonFormatter[ObjectPermissionEnums.Name.type] = {
    new EnumJsonFormatter(ObjectPermissionEnums.Name)
  }
  implicit val dataPermissionName
      : EnumJsonFormatter[DataPermissionEnums.Permission.type] = {
    new EnumJsonFormatter(DataPermissionEnums.Permission)
  }
  implicit val trialType: EnumJsonFormatter[TrialType.type] = {
    new EnumJsonFormatter(TrialType)
  }

  implicit val connectResponse: RootJsonFormat[Connection] = jsonFormat7(
    Connection
  )
  implicit val auth: RootJsonFormat[Auth] = jsonFormat3(Auth)
  implicit val userInfo: RootJsonFormat[UserInfo] = jsonFormat7(UserInfo)
  implicit val errorResponse: RootJsonFormat[ErrorResponse] = jsonFormat3(
    ErrorResponse
  )

  // spray.json jsonFormat cannot parse more than 22 fields, custom format is needed
  implicit object ConnectionRequestJsonFormat
      extends RootJsonFormat[ConnectRequest] {
    private def OptionJsString(value: Option[String]) =
      value.map(JsString(_)).getOrElse(JsNull)
    private def OptionJsBoolean(value: Option[Boolean]) =
      value.map(JsBoolean(_)).getOrElse(JsNull)

    private def getString(fields: Map[String, JsValue], name: String): String =
      fields.get(name) match {
        case Some(JsString(value)) => value
        case _ => throw DeserializationException(s"$name should be string")
      }

    private def getNumber(
        fields: Map[String, JsValue],
        name: String
    ): BigDecimal =
      fields.get(name) match {
        case Some(JsNumber(value)) => value
        case _ => throw DeserializationException(s"$name should be number")
      }

    private def getBool(fields: Map[String, JsValue], name: String): Boolean =
      fields.get(name) match {
        case Some(JsBoolean(value)) => value
        case _ => throw DeserializationException(s"$name should be boolean")
      }

    private def getOptionString(
        fields: Map[String, JsValue],
        name: String
    ): Option[String] =
      fields.get(name) match {
        case Some(JsString(value)) => Some(value)
        case Some(JsNull) | None   => None
        case _ => throw DeserializationException(s"$name should be string")
      }

    private def getOptionBoolean(
        fields: Map[String, JsValue],
        name: String
    ): Option[Boolean] =
      fields.get(name) match {
        case Some(JsBoolean(value)) => Some(value)
        case Some(JsNull) | None    => None
        case _ => throw DeserializationException(s"$name should be boolean")
      }

    def write(request: ConnectRequest): JsValue = JsObject(
      "user_info" -> request.user_info.toJson,
      "connection_id" -> OptionJsString(request.connection_id),
      "hostname" -> JsString(request.hostname),
      "port" -> JsNumber(request.port),
      "workspace_url" -> JsString(request.workspace_url),
      "http_path" -> OptionJsString(request.http_path),
      "jdbc_url" -> OptionJsString(request.jdbc_url),
      "databricks_jdbc_url" -> OptionJsString(request.databricks_jdbc_url),
      "workspace_id" -> JsNumber(request.workspace_id),
      "demo" -> JsBoolean(request.demo),
      "cloud_provider" -> request.cloud_provider.toJson,
      "cloud_provider_region" -> OptionJsString(request.cloud_provider_region),
      "is_free_trial" -> JsBoolean(request.is_free_trial),
      "destination_location" -> OptionJsString(request.destination_location),
      "catalog_name" -> OptionJsString(request.catalog_name),
      "database_name" -> OptionJsString(request.database_name),
      "cluster_id" -> OptionJsString(request.cluster_id),
      "is_sql_endpoint" -> OptionJsBoolean(request.is_sql_endpoint),
      "is_sql_warehouse" -> OptionJsBoolean(request.is_sql_warehouse),
      "data_source_connector" -> OptionJsString(request.data_source_connector),
      "service_principal_id" -> OptionJsString(request.service_principal_id),
      "service_principal_oauth_secret" -> OptionJsString(
        request.service_principal_oauth_secret
      ),
      "connection_scope" -> request.connection_scope
        .map(_.toJson)
        .getOrElse(JsNull),
      "oauth_u2m_app_id" -> OptionJsString(request.oauth_u2m_app_id)
    )

    implicit val connectRequest: RootJsonFormat[ConnectRequest] =
      ConnectionRequestJsonFormat

    def read(value: JsValue): ConnectRequest = {
      val fields = value.asJsObject.fields
      val scoptOpt = fields.get("connection_scope") match {
        case Some(JsNull) => None
        case Some(v) => Some(v.convertTo[ConnectRequestEnums.ConnectionScope])
        case None    => None
      }

      ConnectRequest(
        user_info = fields("user_info").convertTo[UserInfo],
        connection_id = getOptionString(fields, "connection_id"),
        hostname = getString(fields, "hostname"),
        port = getNumber(fields, "port").toInt,
        workspace_url = getString(fields, "workspace_url"),
        http_path = getOptionString(fields, "http_path"),
        jdbc_url = getOptionString(fields, "jdbc_url"),
        databricks_jdbc_url = getOptionString(fields, "databricks_jdbc_url"),
        workspace_id = getNumber(fields, "workspace_id").toLong,
        demo = getBool(fields, "demo"),
        cloud_provider =
          fields("cloud_provider").convertTo[ConnectRequestEnums.CloudProvider],
        cloud_provider_region =
          getOptionString(fields, "cloud_provider_region"),
        is_free_trial = getBool(fields, "is_free_trial"),
        destination_location = getOptionString(fields, "destination_location"),
        catalog_name = getOptionString(fields, "catalog_name"),
        database_name = getOptionString(fields, "database_name"),
        cluster_id = getOptionString(fields, "cluster_id"),
        is_sql_endpoint = getOptionBoolean(fields, "is_sql_endpoint"),
        is_sql_warehouse = getOptionBoolean(fields, "is_sql_warehouse"),
        data_source_connector =
          getOptionString(fields, "data_source_connector"),
        service_principal_id = getOptionString(fields, "service_principal_id"),
        service_principal_oauth_secret =
          getOptionString(fields, "service_principal_oauth_secret"),
        connection_scope = scoptOpt,
        oauth_u2m_app_id = getOptionString(fields, "oauth_u2m_app_id")
      )
    }
  }

  implicit val deleteConnectionRequest
      : RootJsonFormat[DeleteConnectionRequest] = jsonFormat3(
    DeleteConnectionRequest
  )
  implicit val deleteConnectionResponse
      : RootJsonFormat[DeleteConnectionResponse] = jsonFormat1(
    DeleteConnectionResponse
  )

  implicit val accountInfo: RootJsonFormat[AccountInfo] = jsonFormat3(
    AccountInfo
  )
  implicit val resourceToProvision: RootJsonFormat[ResourceToProvision] =
    jsonFormat1(ResourceToProvision)
  implicit val dataPermission: RootJsonFormat[DataPermission] = jsonFormat1(
    DataPermission
  )
  implicit val objectPermission: RootJsonFormat[ObjectPermission] = jsonFormat2(
    ObjectPermission
  )
  implicit val endpoints: RootJsonFormat[PartnerConfigEndpoints] = jsonFormat6(
    PartnerConfigEndpoints
  )
  implicit val testWorkspace: RootJsonFormat[PartnerConfigTestWorkspaceDetail] =
    jsonFormat6(
      PartnerConfigTestWorkspaceDetail
    )
  implicit val partnerConfig: RootJsonFormat[PartnerConfig] = jsonFormat21(
    PartnerConfig
  )
  implicit val testResult: RootJsonFormat[TestResult] = jsonFormat3(TestResult)
  implicit val testResults: RootJsonFormat[ConnectionTestResult] = jsonFormat1(
    ConnectionTestResult
  )
  implicit val connector: RootJsonFormat[Connector] = jsonFormat4(Connector)
  implicit val connectors: RootJsonFormat[ConnectorResponse] = jsonFormat2(
    ConnectorResponse
  )
  implicit val accountUser: RootJsonFormat[AccountUserInfo] = jsonFormat4(
    AccountUserInfo
  )
}
