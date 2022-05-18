package com.databricks.partnerconnect.example.service

import org.openapitools.client.model.ConnectRequestEnums.CloudProvider

import java.util.UUID
import scala.collection.mutable.ArrayBuffer

/** Test Connection service for tracking connections. Note: This test
  * implementation assumes a globally unique connection identifier
  */
class ConnectionService() {
  var connections: ArrayBuffer[ConnectionInfo] = ArrayBuffer()

  def createConnection(
      accountId: String,
      workspaceId: Long,
      cloudProvider: CloudProvider
  ): ConnectionInfo = {
    val connection = ConnectionInfo(
      UUID.randomUUID().toString,
      workspaceId,
      cloudProvider,
      accountId
    )
    connections.append(connection)
    connection
  }

  def getConnection(id: String): Option[ConnectionInfo] = {
    connections.find(_.id == id)
  }

  def getConnections(
      workspaceId: Long,
      cloudProvider: CloudProvider
  ): Seq[ConnectionInfo] = {
    connections.filter(x => {
      x.workspaceId == workspaceId && x.cloudProvider == cloudProvider
    })
  }

  def deleteConnection(id: String): Unit = {
    connections = connections.filter(_.id != id)
  }

  def deleteConnections(accountId: String): Unit = {
    connections = connections.filter(_.accountId != accountId)
  }
}
