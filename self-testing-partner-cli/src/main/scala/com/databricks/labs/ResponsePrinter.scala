package com.databricks.labs

import requests.Response

import scala.util.{Failure, Success, Try}

object ResponsePrinter {
  def printResponse(response: Try[Response], commandType: CommandType): Unit = {
    response match {
      case Success(response) =>
        response.statusCode match {
          case 200 =>
            printSuccessMessage(commandType, response)
          case _ =>
            printErrorResponseMessage(commandType, response)
        }
      case Failure(exception) =>
        printExceptionMessage(exception)
    }
  }

  def printSuccessMessage(command: CommandType, response: Response): Unit = {
    command match {
      case Create() =>
        val uuid = ujson.read(response).obj("id").str
        println(
          s"SUCCESS: Successfully created self-testing partner with UUID: $uuid"
        )
        println(
          "Go to your workspace to see the test tile."
        )
      case Get() =>
        // if there are no active self-testing partners, the key "partners" will not be in the response object
        val partnersOpt = ujson.read(response).obj.get("partners")
        partnersOpt match {
          case Some(partners) =>
            println("Self-Testing Partners:")
            partners.arr.zipWithIndex.foreach { case (partner, idx) =>
              val partnerObj = partner.obj
              val id = partnerObj("id")
              val name = partnerObj("name")
              println(s"${idx + 1}. ID: $id\tName: $name")
            }
          case None => println("No active self-testing partners were found.")
        }
      case Update() =>
        println(
          "SUCCESS: Successfully updated the self-testing partner."
        )
        println(
          "Go to your workspace to see the changes."
        )
      case Delete() =>
        println(
          "SUCCESS: Successfully deleted the self-testing partner."
        )
        println(
          "Go to your workspace to see the changes."
        )
    }
  }

  def printErrorResponseMessage(
      command: CommandType,
      response: Response
  ): Unit = {
    println(
      s"ERROR: Could not ${command.name} self testing partner."
    )
    response match {
      // case is matched for known errors coming from the CRUD APIs (e.g. "Workspace admin is required")
      case response
          if response.contentType.nonEmpty && response.contentType.get == "application/json" && ujson
            .read(response)
            .obj
            .contains("message") =>
        printStatusCodeAndMessage(
          response.statusCode,
          ujson.read(response).obj("message").value.asInstanceOf[String]
        )
      // case is matched if the access token is invalid
      case response
          if response.headers.contains("x-databricks-reason-phrase") =>
        printStatusCodeAndMessage(
          response.statusCode,
          response.headers("x-databricks-reason-phrase").mkString
        )
      // for other errors, print out the status code and status message
      case _ =>
        printStatusCodeAndMessage(
          response.statusCode,
          response.statusMessage
        )

    }
  }

  def printStatusCodeAndMessage(
      statusCode: Int,
      message: String
  ): Unit = {
    println(s"Received status code $statusCode with message: $message")
  }

  def printExceptionMessage(exception: Throwable): Unit = {
    println(
      s"ERROR: Received ${exception.getClass.getSimpleName} with message: ${exception.getMessage}"
    )
  }

  def printNoActiveSelfTestingPartnerMessage(): Unit = {
    println(
      s"ERROR: There is no active self testing partner."
    )
  }
}
