package com.databricks.partnerconnect.client.invoker

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import com.databricks.partnerconnect.example.util.ServiceLogger.entityToString
import com.typesafe.scalalogging.Logger
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.openapitools.client.core.ApiInvoker.ApiMethodExtensions
import org.openapitools.client.core.ParametersMap.ParametersMapImprovements
import org.openapitools.client.core._
import org.openapitools.client.model.PartnerConfig

import scala.collection.immutable
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object PartnerApiInvoker {
  def apply(serializers: Iterable[Serializer[_]], partnerConfig: PartnerConfig)(
      implicit system: ActorSystem
  ): ApiInvoker =
    apply(DefaultFormats ++ Serializers.all ++ serializers, partnerConfig)

  def apply(formats: Formats, partnerConfig: PartnerConfig)(implicit
      system: ActorSystem
  ): ApiInvoker =
    new PartnerApiInvoker(formats, partnerConfig)
}

/** * Custom ApiInvoker is required to overcome limitation of auto gen invoker
  * including:
  *   - it doesn't allow changing the operation path.
  *   - it doesn't allow sending request body for DELETE http method.
  */
class PartnerApiInvoker(formats: Formats, partnerConfig: PartnerConfig)(implicit
    system: ActorSystem
) extends ApiInvoker(formats)(system) {

  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val serialization: Serialization = jackson.Serialization
  private val timeoutSec = 5.seconds

  private val http = Http()
  val logger: Logger = Logger[PartnerApiInvoker]

  val endpointMap: Map[String, Option[String]] = Map(
    "connect" -> Some(partnerConfig.endpoints.connect_path),
    "test-connection" -> partnerConfig.endpoints.test_connection_path,
    "delete-account" -> partnerConfig.endpoints.delete_account_path,
    "expire-account" -> partnerConfig.endpoints.expire_account_path,
    "delete-connection" -> partnerConfig.endpoints.delete_connection_path,
    "connectors" -> partnerConfig.endpoints.get_connectors_path
  )

  override def makeUri(r: ApiRequest[_]): Uri = {
    // Override operationPath based on partner config.
    val opPath =
      getOperationPath(r.operationPath).replaceAll("\\{format\\}", "json")
    val opPathWithParams = r.pathParams.asFormattedParams
      .mapValues(_.toString)
      .foldLeft(opPath) { case (path, (name, value)) =>
        path.replaceAll(s"\\{$name\\}", value)
      }
    val query = makeQuery(r)

    Uri(r.basePath + opPathWithParams).withQuery(query)
  }

  override def execute[T: Manifest](
      r: ApiRequest[T]
  ): Future[ApiResponse[T]] = {
    implicit val timeout: Timeout = settings.connectionTimeout
    val request = createRequest(makeUri(r), r)
    http
      .singleRequest(request)
      .map { response =>
        val decoder: Coder with StreamDecoder = response.encoding match {
          case HttpEncodings.gzip =>
            Gzip
          case HttpEncodings.deflate =>
            Deflate
          case HttpEncodings.identity =>
            NoCoding
          case HttpEncoding(encoding) =>
            throw new IllegalArgumentException(
              s"Unsupported encoding: $encoding"
            )
        }
        val message = decoder.decodeMessage(response)
        // Convert to strict entity so stream is read only once.
        val strictEntity: ResponseEntity = toStrict(message.entity)
        logger.info(
          s"Response code: ${message.status}, " +
            s"body: ${entityToString(strictEntity)}, headers: ${message.headers}"
        )
        message.withEntity(strictEntity)
      }
      .flatMap(unmarshallApiResponse(r))
  }

  /** This function is copied from ApiInvoker. With the new version of the code
    * gen the unmarshal method doesn't include the json body of failure reponse.
    * Copied and modified this method here to override how response body is read
    * from entity.
    */
  override def unmarshallApiResponse[T: Manifest](
      request: ApiRequest[T]
  )(response: HttpResponse): Future[ApiResponse[T]] = {
    def responseForState[V](state: ResponseState, value: V): ApiResponse[V] = {
      state match {
        case ResponseState.Success =>
          ApiResponse(
            response.status.intValue,
            value,
            response.headers.map(header => (header.name, header.value)).toMap
          )
        case ResponseState.Error =>
          throw ApiError(
            response.status.intValue,
            "Error response received",
            Some(value),
            headers =
              response.headers.map(header => (header.name, header.value)).toMap
          )
      }
    }
    val mf = implicitly(manifest[T])
    request
      .responseForCode(response.status.intValue) match {
      case Some((Manifest.Unit, state: ResponseState)) =>
        Future(responseForState(state, ()).asInstanceOf[ApiResponse[T]])
      case Some((manifest, state: ResponseState)) if manifest == mf =>
        implicit val m: Unmarshaller[HttpEntity, T] =
          unmarshaller[T](mf, serialization, formats)
        Unmarshal(response.entity)
          .to[T]
          .recoverWith { case e =>
            throw ApiError(
              response.status.intValue,
              s"Unable to unmarshall content to [$manifest]",
              Some(entityToString(toStrict(response.entity))),
              e
            )
          }
          .map(value => responseForState(state, value))
      case None | Some(_) =>
        Future.failed(
          ApiError(
            response.status.intValue,
            "Unexpected response code",
            Some(entityToString(toStrict(response.entity)))
          )
        )
    }
  }

  private def getOperationPath(original: String): String = {
    val cleanOpt = original.replaceAll("/", "")
    val message = s"Operation ${cleanOpt} not found in ${endpointMap}"
    assert(endpointMap.contains(cleanOpt), message)
    assert(endpointMap(cleanOpt).isDefined, message)
    endpointMap(cleanOpt).get
  }

  /** * This and private methods below are copied from the code gen ApiInvoker
    * and changed to allow DELETE to send request body.
    */
  private def createRequest(uri: Uri, request: ApiRequest[_]): HttpRequest = {
    val httpRequest = request.method.toAkkaHttpMethod match {
      case m @ (HttpMethods.GET) => HttpRequest(m, uri)
      // Allow delete to send body in request payload.
      case m @ (HttpMethods.POST | HttpMethods.DELETE | HttpMethods.PUT |
          HttpMethods.PATCH) =>
        bodyContent(request) match {
          case Some(c: FormData) =>
            HttpRequest(m, uri, entity = c.toEntity)
          case Some(c: Multipart.FormData) =>
            HttpRequest(m, uri, entity = c.toEntity)
          case Some(c: String) =>
            HttpRequest(
              m,
              uri,
              entity = HttpEntity(
                normalizedContentType(request.contentType),
                ByteString(c)
              )
            )
          case _ =>
            HttpRequest(
              m,
              uri,
              entity = HttpEntity(
                normalizedContentType(request.contentType),
                ByteString(" ")
              )
            )
        }
      case m: HttpMethod => HttpRequest(m, uri)
    }

    addAuthentication(request.credentials)(
      httpRequest.withHeaders(headers(request.headerParams))
    )
  }

  private def addAuthentication(credentialsSeq: Seq[Credentials]) = {
    request: HttpRequest =>
      credentialsSeq.foldLeft(request) {
        case (req, BasicCredentials(login, password)) =>
          req.addHeader(Authorization(BasicHttpCredentials(login, password)))
        case (
              req,
              ApiKeyCredentials(keyValue, keyName, ApiKeyLocations.HEADER)
            ) =>
          req.addHeader(RawHeader(keyName, keyValue.value))
        case (req, BearerToken(token)) =>
          req.addHeader(RawHeader("Authorization", s"Bearer $token"))
        case (req, _) => req
      }
  }

  private def headers(headers: Map[String, Any]): immutable.Seq[HttpHeader] =
    headers.asFormattedParams
      .map { case (name, value) => RawHeader(name, value.toString) }
      .to[immutable.Seq]

  private def bodyContent(request: ApiRequest[_]): Option[Any] =
    request.bodyParam
      .map(Extraction.decompose)
      .map(compact)

  private def toStrict(entity: ResponseEntity): ResponseEntity = {
    Await.result(entity.toStrict(timeoutSec), timeoutSec)
  }
}
