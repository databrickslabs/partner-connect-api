package com.databricks.partnerconnect.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.{ActorMaterializer, Materializer}
import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import com.databricks.partnerconnect.example.handlers.{
  ConnectHandler,
  DeleteAccountHandler,
  DeleteConnectionHandler,
  ExpireAccountHandler,
  GetConnectorHandler,
  TestConnectionHandler
}
import com.databricks.partnerconnect.example.service.{
  AccountService,
  ConnectionService
}
import com.databricks.partnerconnect.example.util.PartnerConfigUtil
import com.databricks.partnerconnect.example.util.ServiceLogger.withRequestLogging
import com.databricks.partnerconnect.example.validators.{
  ConnectValidator,
  ConnectionInfoValidator,
  DeleteAccountValidator,
  ExpireAccountValidator
}
import com.typesafe.scalalogging.Logger
import org.openapitools.client.model.{
  AccountInfo,
  AccountUserInfo,
  ConnectRequest,
  ConnectionInfo,
  PartnerConfig
}

import java.net.URI
import scala.concurrent.ExecutionContext

/** Partner Connect Test Server.
  */
case class Server(config: PartnerConfig) {
  implicit val system: ActorSystem =
    akka.actor.ActorSystem("PartnerConnectServer")
  implicit val rejectionHandler = PartnerRejectionHandler.rejectionHandler
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext =
    materializer.executionContext

  val logger: Logger = Logger(getClass.getName)
  val accountService = new AccountService()
  val connectionService = new ConnectionService()
  val connectionHandler =
    new ConnectHandler(accountService, connectionService, config)
  val deleteConnectionHandler = new DeleteConnectionHandler(connectionService)
  val deleteAccountHandler =
    new DeleteAccountHandler(accountService, connectionService)
  val expireAccountHandler =
    new ExpireAccountHandler(accountService)
  val connectValidator = new ConnectValidator(config)
  val connectionInfoValidator = new ConnectionInfoValidator()
  val deleteAccountValidator = new DeleteAccountValidator()
  val testConnectionHandler = new TestConnectionHandler(connectionService)
  val getConnectorHandler = new GetConnectorHandler(config)
  val expireAccountValidator = new ExpireAccountValidator()

  def startServer(): Unit = {
    val route = handleRejections(rejectionHandler) {
      withRequestLogging(createRoute)
    }
    logger.info(s"Server online at ${config.base_url}")
    Http().bindAndHandle(
      route,
      "localhost",
      new URI(config.base_url).getPort
    )
  }

  def stopServer: Unit = {
    system.terminate()
  }

  val connectRoute: Route = path("connect") {
    post {
      entity(as[ConnectRequest]) { connection =>
        val validation = connectValidator.validate(connection)
        validate(validation.valid, validation.toString) {
          connectionHandler.handle(connection)
        }
      }
    }
  }

  val deleteConnectionRoute: Route = path("delete-connection") {
    delete {
      entity(as[ConnectionInfo]) { connection =>
        val validation =
          connectionInfoValidator.validate(connection)
        validate(validation.valid, validation.toString) {
          deleteConnectionHandler.handle(connection)
        }
      }
    }
  }

  val deleteAccountRoute: Route = path("delete-account") {
    delete {
      entity(as[AccountInfo]) { account =>
        val validation =
          deleteAccountValidator.validate(account)
        validate(validation.valid, validation.toString) {
          deleteAccountHandler.handle(account)
        }
      }
    }
  }

  val expireAccountRoute: Route = path("expire-account") {
    put {
      entity(as[AccountUserInfo]) { accountUser =>
        val validation =
          expireAccountValidator.validate(accountUser)
        validate(validation.valid, validation.toString) {
          expireAccountHandler.handle(accountUser)
        }
      }
    }
  }

  val testConnectionRoute: Route = path("test-connection") {
    post {
      entity(as[ConnectionInfo]) { connectionInfo =>
        val validation =
          connectionInfoValidator.validate(connectionInfo)
        validate(validation.valid, validation.toString) {
          testConnectionHandler.handle(connectionInfo)
        }
      }
    }
  }

  val getConnectorsRoute: Route = path("connectors") {
    get {
      parameter("pagination_token") { p =>
        logger.info(s"Getting pagination token: ${p}")
        getConnectorHandler.handle(p)
      }
    }
  }

  val trial: Route = path("trial") {
    get {
      complete("OK")
    }
  }
  val signIn: Route = path("signin") {
    get {
      complete("OK")
    }
  }
  val join: Route = path("join") {
    get {
      complete(StatusCodes.PermanentRedirect, "Redirect")
    }
  }

  private def createRoute = {
    // Authenticate the request and extract the userName on successful request
    // or reject with 401 if invalid credential is passed.
    concat(
      authenticateBasic(realm = "secure", authenticator = authenticator) {
        userName =>
          headerValueByName("User-Agent") { userAgent =>
            logger.info(
              s"Authenticated ${userName} with User-Agent: ${userAgent}"
            )
            concat(
              connectRoute,
              deleteConnectionRoute,
              expireAccountRoute,
              deleteAccountRoute,
              testConnectionRoute,
              getConnectorsRoute
            )
          }
      },
      trial,
      signIn,
      join
    )
  }

  private def authenticator(credentials: Credentials): Option[String] =
    credentials match {
      case p @ Credentials.Provided(id) =>
        if (
          id == PartnerConfigUtil.getBasicAuthUser() && p.verify(
            PartnerConfigUtil.getBasicAuthPassword()
          )
        ) Some(id)
        else None
      case _ => None
    }
}

/** Main class for running the server from command line or docker.
  */
object Server {
  def main(args: Array[String]): Unit = {
    println(s"Running server with arguments: ${args.mkString(",")}")
    if (args.length < 1) {
      System.err.println(
        "Usage mvn exec:java -Dexec.args=<partner_config_file>\n" +
          "Example: mvn exec:java -Dexec.args=test.json"
      )
      System.exit(-1)
    }
    Server(PartnerConfigUtil.getPartnerConfig(Some(args(0)))).startServer()
  }
}
