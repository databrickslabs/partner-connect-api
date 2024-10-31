package com.databricks.labs

import com.databricks.labs.StringConstants.{
  configFileName,
  configFilePathEnvVariable,
  hostEnvVariable,
  tokenEnvVariable
}

import scala.io.Source
import scala.util.{Try, Using}

object DatabricksConfigParser {

  /** Retrieves the host and token if they exist
    * @param useEnvVariables
    *   If true, we use environment variables. Otherwise we use the
    *   configuration profiles file
    * @param configurationProfileName
    *   The name of the configuration profiles file to search
    * @param sysEnv
    *   The environment variables (i.e. sys.env)
    * @return
    */
  def getHostAndToken(
      useEnvVariables: Boolean,
      configurationProfileName: String,
      sysEnv: Map[String, String]
  ): (Option[String], Option[String]) = {
    val (host, token) = if (useEnvVariables) getConfigFromEnvVariables(sysEnv) else getDatabricksConfigFromProfile(configurationProfileName, sysEnv)
    return (host.map(_.stripSuffix("/")), token)
  }

  /** Finds the value for a field in a line from a Databricks configuration
    * profile, if it exists
    * @param field
    *   The name of the field (e.g. "host", "token)
    * @param line
    *   The line which should contain the field and its value
    * @return
    *   The value of the field or None if not found
    */
  private def getFieldFromConfig(
      field: String,
      line: String
  ): Option[String] = {
    // Pattern matches: field + >=0 whitespace characters + = + >=0 whitespace characters + 1 or more characters
    // e.g. if field == "host", this would match "host = https:dev.databricks.com"
    // The (.+) is a capturing group which allows us to extract the value
    // Issue for IntelliJ warning: https://youtrack.jetbrains.com/issue/IDEA-314646
    val pattern = s"$field\\s*=\\s*(.+)".r
    line match {
      case pattern(value) => Some(value)
      case _ =>
        println(
          s"ERROR: Unable to find value for $field from $configFileName."
        )
        None
    }
  }

  /** Reads the host and token from the Databricks configurations profile for
    * the specified profile name
    * @param profile
    *   The name of the profile (e.g. "DEFAULT")
    * @param sysEnv
    *   The environment variables (i.e. sys.env)
    * @return
    *   The host and token if they exist
    */
  private def getDatabricksConfigFromProfile(
      profile: String,
      sysEnv: Map[String, String]
  ): (Option[String], Option[String]) = {
    val home = System.getProperty("user.home")
    val configFilePath = sysEnv.getOrElse(
      configFilePathEnvVariable,
      s"$home/$configFileName"
    )
    var host: Option[String] = None
    var token: Option[String] = None

    Try({
      // Using will close the resource automatically
      Using.resource(Source.fromFile(configFilePath)) { dbConfigFile =>
        val profileWithBrackets = s"[$profile]"
        // advances the iterator until we find a line with the profile in it or there are no more lines
        val lines =
          dbConfigFile.getLines.dropWhile(!_.contains(profileWithBrackets))
        // if there are no more lines then we did not find the profile in the config file
        if (!lines.hasNext) {
          println(
            s"ERROR: Unable to find profile $profileWithBrackets in $configFilePath."
          )
        } else {
          // advance the iterator past the profile line
          lines.drop(1)
          // try to read the host and token from the following lines
          if (lines.hasNext) host = getFieldFromConfig("host", lines.next())
          if (lines.hasNext) token = getFieldFromConfig("token", lines.next())
        }
      }
    }).recover({ case e: Throwable =>
      println(
        s"ERROR: Received ${e.getClass.getSimpleName} for ${e.getMessage}."
      )
      println(
        "Please ensure that authentication has been set up."
      )
    })
    (host, token)
  }

  /** Retrieves the value for an environment variable if it exists
    * @param sysEnv
    *   The environment variables (i.e. sys.env)
    * @param envVariable
    *   The environment variable we want to retrieve the value for
    * @return
    *   The value of the environment variable or None if not found
    */
  private def getValueFromEnvVariable(
      sysEnv: Map[String, String],
      envVariable: String
  ): Option[String] = {
    sysEnv.get(envVariable) match {
      case Some(value) => Some(value)
      case None =>
        println(
          s"ERROR: Unable to read $envVariable."
        )
        None
    }
  }

  /** Reads the host and token from their respective environment variables
    * @param sysEnv
    *   The environment variables (i.e. sys.env)
    * @return
    *   The host and token if they exist
    */
  private def getConfigFromEnvVariables(
      sysEnv: Map[String, String]
  ): (Option[String], Option[String]) = {
    val host = getValueFromEnvVariable(sysEnv, hostEnvVariable)
    val token = getValueFromEnvVariable(sysEnv, tokenEnvVariable)
    (host, token)
  }
}
