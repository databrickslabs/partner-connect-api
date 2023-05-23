package com.databricks.labs

import com.databricks.labs.DatabricksConfigParser.getHostAndToken
import com.databricks.labs.IdRetriever.getIdForCommand
import com.databricks.labs.RequestClient.{
  createRequest,
  deleteRequest,
  getRequest,
  updateRequest
}
import com.databricks.labs.ResponsePrinter.printResponse
import mainargs._

object Main {
  @main
  case class CLIConfig(
      @arg(name = "env", short = 'e', doc = "Use environment variables")
      useEnvVariables: Flag = Flag(false),
      @arg(
        name = "profile",
        short = 'p',
        doc = "Profile to use in the Databricks config file"
      )
      profile: String = "DEFAULT"
  )
  implicit def configParser: ParserForClass[CLIConfig] =
    ParserForClass[CLIConfig]

  @main
  def create(
      cliConfig: CLIConfig,
      @arg(
        name = "file",
        short = 'f',
        doc = "Self-testing partner config file name"
      )
      fileName: String
  ): Unit = {
    getHostAndToken(
      cliConfig.useEnvVariables.value,
      cliConfig.profile,
      sys.env
    ) match {
      // if the host and token were both successfully retrieved, we make the request to the API
      case (Some(host), Some(token)) =>
        val createResponse = createRequest(host, token, fileName)
        printResponse(createResponse, Create())
      // otherwise, we have already printed the error response and do nothing else
      case _ =>
    }
  }

  @main
  def get(
      cliConfig: CLIConfig
  ): Unit = {
    getHostAndToken(
      cliConfig.useEnvVariables.value,
      cliConfig.profile,
      sys.env
    ) match {
      case (Some(host), Some(token)) =>
        val getResponse = getRequest(host, token)
        printResponse(getResponse, Get())
      case _ =>
    }
  }

  @main
  def update(
      cliConfig: CLIConfig,
      @arg(
        name = "file",
        short = 'f',
        doc = "Self-testing partner config file name"
      )
      fileName: String
  ): Unit = {
    getHostAndToken(
      cliConfig.useEnvVariables.value,
      cliConfig.profile,
      sys.env
    ) match {
      case (Some(host), Some(token)) =>
        val idToUpdate = getIdForCommand(host, token)
        idToUpdate.foreach { id =>
          val updateResponse =
            updateRequest(host, token, fileName, id)
          printResponse(updateResponse, Update())
        }
      case _ =>
    }
  }

  @main
  def delete(
      cliConfig: CLIConfig
  ): Unit = {
    getHostAndToken(
      cliConfig.useEnvVariables.value,
      cliConfig.profile,
      sys.env
    ) match {
      case (Some(host), Some(token)) =>
        val idToDelete = getIdForCommand(host, token)
        idToDelete.foreach { id =>
          val deleteResponse = deleteRequest(host, token, id)
          printResponse(deleteResponse, Delete())
        }
      case _ =>
    }
  }

  def main(args: Array[String]): Unit = {
    ParserForMethods(this).runOrExit(args)
  }
}
