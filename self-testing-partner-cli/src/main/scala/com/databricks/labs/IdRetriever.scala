package com.databricks.labs

import com.databricks.labs.RequestClient.getRequest
import com.databricks.labs.ResponsePrinter.{
  printErrorResponseMessage,
  printExceptionMessage,
  printNoActiveSelfTestingPartnerMessage
}

import scala.util.{Failure, Success}

object IdRetriever {
  // gets ID of self-testing partner to be updated/deleted if it exists
  // otherwise, print message indicating why there is no ID returned
  def getIdForCommand(
      host: String,
      token: String
  ): Option[String] = {
    val getResponse = getRequest(host, token)
    var idToUpdate: Option[String] = None
    getResponse match {
      case Success(getResponse) =>
        if (getResponse.statusCode == 200) {
          val getResponseData = ujson.read(getResponse).obj
          getResponseData match {
            case data
                if data
                  .contains("partners") && data("partners").arr.nonEmpty =>
              // currently there is max one self-testing partner per workspace
              idToUpdate = Some(data("partners")(0)("id").value.toString)
            case _ =>
              printNoActiveSelfTestingPartnerMessage()
          }
        } else {
          printErrorResponseMessage(Get(), getResponse)
        }
      case Failure(exception) =>
        printExceptionMessage(exception)
    }
    idToUpdate
  }
}
