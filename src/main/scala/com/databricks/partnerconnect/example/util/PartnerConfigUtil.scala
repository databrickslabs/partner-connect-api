package com.databricks.partnerconnect.example.util
import com.databricks.partnerconnect.example.formatters.JsonFormatters._
import com.typesafe.scalalogging.Logger
import org.openapitools.client.model.PartnerConfig
import spray.json._

import java.nio.file.{Files, Paths}
import java.util.UUID
import scala.util.Random

object PartnerConfigUtil {
  val logger = Logger(getClass.getName)

  val CONFIG_NAME = "CONFIG"
  val seed = System.currentTimeMillis()
  logger.info(s"Using seed: ${seed} for random number generation.")
  val random: Random = new Random(seed)
  val testPartners: Seq[String] = Seq(
    "test.json",
    "interactive.json",
    "new_user_error.json",
    "new_user_invite.json",
    "new_user_not_found.json"
  )

  /** Get the partner configuration from the supplied file or the default
    * config.
    */
  def getPartnerConfig(configFileName: Option[String]): PartnerConfig = {
    val fileName = configFileName.getOrElse(getPartnerConfigFile())
    val directory =
      if (testPartners.contains(fileName.toLowerCase)) "partners/test"
      else "partners/prod"

    val configFile =
      Paths
        .get(directory, configFileName.getOrElse(getPartnerConfigFile()))
        .toFile
    assert(configFile.exists, s"Config file ${configFile} is not found.")
    val partnerStr = Files.readString(configFile.toPath)
    val jsValue = partnerStr.parseJson
    jsValue.convertTo[PartnerConfig]
  }

  def getProdConfigExists(): Boolean = {
    Paths.get("partners", "prod").toFile.exists
  }

  def getBasicAuthUser(): String = {
    getEnv("BASIC_USER")
  }

  def getBasicAuthPassword(): String = {
    getEnv("BASIC_PASSWORD")
  }

  def getPAT(): String = {
    getEnv("PAT_TOKEN")
  }

  private def getEnv(name: String): String = {
    if (sys.env.contains(name)) sys.env(name)
    else
      throw new IllegalArgumentException(
        s"Environment variable ${name} is not set"
      )
  }

  /** Get the partner config file.This will look for the file in System
    * properties (-DCONFIG=xyz) or default to test.json
    */
  def getPartnerConfigFile(): String = {
    sys.props.getOrElse(CONFIG_NAME, "test.json")
  }

  def nextLong(): Long = {
    Math.abs(random.nextLong())
  }

  def newEmail(userName: String): String = {
    s"$userName@${UUID.randomUUID().toString}.com"
  }

  def newDomain(): String = {
    s"${UUID.randomUUID().toString}.com"
  }
}
