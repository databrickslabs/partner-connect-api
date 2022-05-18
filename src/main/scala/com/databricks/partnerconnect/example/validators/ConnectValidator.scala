package com.databricks.partnerconnect.example.validators

import org.openapitools.client.model.ResourceToProvisionEnums.ResourceType.{
  InteractiveCluster,
  SqlEndpoint
}
import org.openapitools.client.model.{ConnectRequest, PartnerConfig}

class ConnectValidator(partnerConfig: PartnerConfig)
    extends Validator[ConnectRequest] {
  val requiresSqlEndpoint: Boolean =
    partnerConfig.resources_to_provision.exists(
      _.resource_type == SqlEndpoint
    )
  val requiresInteractiveCluster: Boolean =
    partnerConfig.resources_to_provision.exists(
      _.resource_type == InteractiveCluster
    )
  val requiredClusterId: Boolean =
    requiresSqlEndpoint || requiresInteractiveCluster

  def connectionIdRequired(request: ConnectRequest): Boolean =
    request.user_info.is_connection_established
  def missingConnectionId(request: ConnectRequest): Boolean =
    if (connectionIdRequired(request)) request.connection_id.isEmpty else false

  val validationMap: Map[String, ConnectRequest => Boolean] = Map(
    "user_info" -> (r => r.user_info != null),
    "connection_id" -> (r => !missingConnectionId(r)),
    // If requires sql endpoint, is_sql_endpoint should be set, jdbc_url and http_path should be set.
    "is_sql_endpoint" -> (r =>
      r.is_sql_endpoint.getOrElse(false) == requiresSqlEndpoint
    ),
    "jdbc_url" -> (r =>
      r.jdbc_url.getOrElse("").nonEmpty == requiresSqlEndpoint
    ),
    "http_path" -> (r =>
      r.http_path
        .getOrElse("")
        .nonEmpty == requiresSqlEndpoint
    ),
    "cluster_id" -> (r =>
      r.cluster_id
        .getOrElse("")
        .nonEmpty == requiredClusterId
    )
    // TODO: Add more request validation
  )

  override def validate(request: ConnectRequest): ValidationResult = {
    val invalidFields = validationMap.filterNot(_._2(request))
    ValidationResult(invalidFields.isEmpty, invalidFields.keys.toSeq)
  }
}
