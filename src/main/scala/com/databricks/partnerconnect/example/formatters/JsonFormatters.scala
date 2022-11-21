package com.databricks.partnerconnect.example.formatters

import org.openapitools.client.model.ConnectRequestEnums.CloudProvider
import org.openapitools.client.model.ConnectionEnums.{
  AccountStatus,
  RedirectValue,
  UserStatus
}
import org.openapitools.client.model.ConnectorEnums.{`Type` => ConnectorType}
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason
import org.openapitools.client.model.PartnerConfigEnums.{
  Category,
  NewUserAction,
  TrialType
}
import org.openapitools.client.model.ResourceToProvisionEnums.ResourceType
import org.openapitools.client.model.TestResultEnums.Status
import org.openapitools.client.model.{Connector, _}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonFormatters extends DefaultJsonProtocol {
  // Order of declaration matters. Enums need to be defined first otherwise ProductFormats.scala throws NPE.
  implicit val errorReason: EnumJsonFormatter[ErrorReason.type] =
    new EnumJsonFormatter(ErrorReason)
  implicit val cloudProvider: EnumJsonFormatter[CloudProvider.type] =
    new EnumJsonFormatter(CloudProvider)
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

  implicit val connectResponse: RootJsonFormat[Connection] = jsonFormat6(
    Connection
  )
  implicit val auth: RootJsonFormat[Auth] = jsonFormat3(Auth)
  implicit val userInfo: RootJsonFormat[UserInfo] = jsonFormat7(UserInfo)
  implicit val errorResponse: RootJsonFormat[ErrorResponse] = jsonFormat3(
    ErrorResponse
  )
  implicit val connectRequest: RootJsonFormat[ConnectRequest] = jsonFormat21(
    ConnectRequest
  )
  implicit val connectionInfo: RootJsonFormat[ConnectionInfo] = jsonFormat1(
    ConnectionInfo
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
