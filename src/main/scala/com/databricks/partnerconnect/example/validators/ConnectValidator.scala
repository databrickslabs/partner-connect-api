package com.databricks.partnerconnect.example.validators

import java.util.UUID
import scala.util.Try

import org.openapitools.client.model.ResourceToProvisionEnums.ResourceType.{
  InteractiveCluster,
  SqlEndpoint
}
import org.openapitools.client.model.{ConnectRequest, PartnerConfig}

class ConnectValidator(partnerConfig: PartnerConfig)
    extends Validator[ConnectRequest] {
  val requiresSqlWarehouse: Boolean =
    partnerConfig.resources_to_provision.exists(
      _.resource_type == SqlEndpoint
    )
  val requiresInteractiveCluster: Boolean =
    partnerConfig.resources_to_provision.exists(
      _.resource_type == InteractiveCluster
    )
  val requiresClusterId: Boolean =
    requiresSqlWarehouse || requiresInteractiveCluster

  def hasValidServicePrincipalId(request: ConnectRequest): Boolean =
    request.service_principal_id.isEmpty ||
      Try(UUID.fromString(request.service_principal_id.get)).isSuccess

  def connectionIdRequired(request: ConnectRequest): Boolean =
    request.user_info.is_connection_established
  def missingConnectionId(request: ConnectRequest): Boolean =
    if (connectionIdRequired(request)) request.connection_id.isEmpty else false

  val validationMap: Map[String, ConnectRequest => Boolean] = Map(
    "user_info" -> (r => r.user_info != null),
    "connection_id" -> (r => !missingConnectionId(r)),
    // If requires sql warehouse, is_sql_warehouse should be set, databricks_jdbc_url, jdbc_url and http_path should be set.
    "is_sql_endpoint" -> (r =>
      r.is_sql_endpoint.getOrElse(false) == requiresSqlWarehouse
    ),
    "is_sql_warehouse" -> (r =>
      r.is_sql_warehouse.getOrElse(false) == requiresSqlWarehouse
    ),
    "jdbc_url" -> (r =>
      r.jdbc_url.getOrElse("").nonEmpty == requiresSqlWarehouse
    ),
    "databricks_jdbc_url" -> (r =>
      r.databricks_jdbc_url.getOrElse("").nonEmpty == requiresSqlWarehouse
    ),
    "http_path" -> (r =>
      r.http_path
        .getOrElse("")
        .nonEmpty == requiresSqlWarehouse
    ),
    "cluster_id" -> (r =>
      r.cluster_id
        .getOrElse("")
        .nonEmpty == requiresClusterId
    ),
    "service_principal_id" -> (r => hasValidServicePrincipalId(r))
    // TODO: Add more request validation
  )

  override def validate(request: ConnectRequest): ValidationResult = {
    val invalidFields = validationMap.filterNot(_._2(request))
    ValidationResult(invalidFields.isEmpty, invalidFields.keys.toSeq)
  }
}
