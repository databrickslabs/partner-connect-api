package com.databricks.partnerconnect.example.util

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.{HttpEntity, HttpRequest}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.{DebuggingDirectives, LoggingMagnet}
import akka.stream.Materializer
import akka.util.ByteString
import com.typesafe.scalalogging.Logger

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object ServiceLogger {

  /** Extracts requests and logs it for debugging.
    */
  def withRequestLogging(route: Route)(implicit
      m: Materializer,
      ex: ExecutionContext
  ) = {
    def log(loggingAdapter: LoggingAdapter)(req: HttpRequest): Unit = {
      loggingAdapter.info(s"Request body: ${entityToString(req.entity)}")
    }
    DebuggingDirectives.logRequest(LoggingMagnet(log(_)))(route)
  }

  def entityToString(
      entity: HttpEntity
  )(implicit m: Materializer, ex: ExecutionContext): String = {
    val bodyAsBytes: Future[ByteString] =
      entity.toStrict(5.second).map(_.data)
    val bodyAsString: Future[String] = bodyAsBytes.map(_.utf8String)
    Await.result(bodyAsString, 5.second)
  }
}
