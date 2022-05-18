package com.databricks.partnerconnect.client.tests

import akka.actor.ActorSystem
import com.databricks.partnerconnect.client.invoker.PartnerApiInvoker
import com.databricks.partnerconnect.example.util.PartnerConfigUtil
import com.databricks.partnerconnect.example.Server
import com.typesafe.scalalogging.Logger
import org.openapitools.client.api.{
  AccountApi,
  ConnectionApi,
  DatasourceApi,
  EnumsSerializers
}
import org.openapitools.client.core.ApiInvoker
import org.openapitools.client.model.PartnerConfig
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Try

class EnvironmentSetupSuite extends AnyFunSuite with BeforeAndAfterAll {
  var invoker: ApiInvoker = _
  var connectionApi: ConnectionApi = _
  var accountApi: AccountApi = _
  var datasourceApi: DatasourceApi = _
  var server: Server = _

  implicit val system: ActorSystem = ActorSystem()
  val logger: Logger = Logger[EnvironmentSetupSuite]
  // If REMOTE_SERVER env var is set, test runs against remote server.
  val IS_REMOTE = "REMOTE_SERVER"
  val configName: String = PartnerConfigUtil.getPartnerConfigFile()
  val config: PartnerConfig =
    PartnerConfigUtil.getPartnerConfig(Some(configName))
  val isRemote: Boolean =
    if (sys.env.contains(IS_REMOTE))
      Try(sys.env(IS_REMOTE).toBoolean).getOrElse(false)
    else !config.base_url.contains("localhost")

  logger.info(
    s"Running tests with partner config: ${configName}, isRemoteServer:$isRemote"
  )

  override protected def beforeAll(): Unit = {
    invoker = PartnerApiInvoker(EnumsSerializers.all, config)
    connectionApi = ConnectionApi(config.base_url)
    accountApi = AccountApi(config.base_url)
    datasourceApi = DatasourceApi(config.base_url)
    if (!isRemote) {
      server = Server(config)
      logger.info("Starting server...")
      server.startServer()
    }
  }

  override def afterAll(): Unit = {
    if (!isRemote) {
      server.stopServer
    }
  }
}
