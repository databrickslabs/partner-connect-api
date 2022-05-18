package com.databricks.partnerconnect.example.service

import org.openapitools.client.model.ConnectRequestEnums.CloudProvider

/** Represents a connection to give workspace
  */
case class ConnectionInfo(
    id: String,
    workspaceId: Long,
    cloudProvider: CloudProvider,
    accountId: String
)
