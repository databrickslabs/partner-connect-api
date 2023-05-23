package com.databricks.labs

import com.databricks.labs.StringConstants.selfTestPartnersEndpoint
import requests.RequestAuth

import scala.io.Source
import scala.util.Try

object RequestClient {
  def createRequest(
      host: String,
      token: String,
      fileName: String
  ): Try[requests.Response] = {
      Try({
        val source = Source.fromFile(fileName)
        val selfTestingPartnerConfig = source.mkString
        source.close()
        val createSelfTestingPartnerUrl =
          s"$host/$selfTestPartnersEndpoint/create"
        requests.post(
          url = createSelfTestingPartnerUrl,
          auth = RequestAuth.Bearer(token),
          data = selfTestingPartnerConfig,
          check = false
        )
      })
  }

  def getRequest(
      host: String,
      token: String
  ): Try[requests.Response] = {
    val getSelfTestingPartnersUrl = s"$host/$selfTestPartnersEndpoint/get"
    val response =
      Try({
        requests.get(
          url = getSelfTestingPartnersUrl,
          auth = RequestAuth.Bearer(token),
          check = false
        )
      })
    response
  }

  def updateRequest(
      host: String,
      token: String,
      fileName: String,
      id: String
  ): Try[requests.Response] = {
    val response =
      Try({
        val source = Source.fromFile(fileName)
        val selfTestingPartnerConfig = source.mkString
        source.close()
        val updateSelfTestingPartnerUrl =
          s"$host/$selfTestPartnersEndpoint/update/$id"
        requests.put(
          url = updateSelfTestingPartnerUrl,
          auth = RequestAuth.Bearer(token),
          data = selfTestingPartnerConfig,
          check = false
        )
      })
    response
  }

  def deleteRequest(
      host: String,
      token: String,
      id: String
  ): Try[requests.Response] = {
    val deleteSelfTestingPartnersUrl =
      s"$host/$selfTestPartnersEndpoint/delete/$id"
    val response =
      Try({
        requests.delete(
          url = deleteSelfTestingPartnersUrl,
          auth = RequestAuth.Bearer(token),
          check = false
        )
      })
    response
  }
}
