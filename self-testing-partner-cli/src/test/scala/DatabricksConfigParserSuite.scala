import com.databricks.labs.DatabricksConfigParser.getHostAndToken
import com.databricks.labs.StringConstants.{
  configFilePathEnvVariable,
  hostEnvVariable,
  tokenEnvVariable
}
import org.scalatest.funsuite.AnyFunSuite

class DatabricksConfigParserSuite extends AnyFunSuite {
  private val testHost = "defaulthost"
  private val testToken = "defaulttoken"

  test("PROFILE - Successfully gets host/token from profile") {
    val profileName = "DEFAULT"
    val stream = new java.io.ByteArrayOutputStream()
    val env =
      Map(configFilePathEnvVariable -> getClass.getResource(".testcfg").getPath)
    val (host, token) = Console.withErr(stream) {
      getHostAndToken(useEnvVariables = false, profileName, env)
    }
    assert(stream.toString.isEmpty)
    assert(host.get == testHost)
    assert(token.get == testToken)
    stream.close()
  }

  test("PROFILE - Error when unable to find profile") {
    val missingProfileName = "MISSING"
    val stream = new java.io.ByteArrayOutputStream()
    val env =
      Map(configFilePathEnvVariable -> getClass.getResource(".testcfg").getPath)
    Console.withOut(stream) {
      getHostAndToken(
        useEnvVariables = false,
        missingProfileName,
        env
      )
    }
    assert(
      stream
        .toString()
        .contains(
          s"ERROR: Unable to find profile [$missingProfileName] in ${env(configFilePathEnvVariable)}"
        )
    )
    stream.close()
  }

  test(
    "ENVIRONMENT VARIABLES - Successfully gets host/token from env variables"
  ) {
    val env = Map(hostEnvVariable -> testHost, tokenEnvVariable -> testToken)
    val stream = new java.io.ByteArrayOutputStream()
    val (host, token) = Console.withOut(stream) {
      getHostAndToken(useEnvVariables = true, "default", env)
    }
    assert(stream.toString.isEmpty)
    assert(host.get == testHost)
    assert(token.get == testToken)
    stream.close()
  }

  test("ENVIRONMENT VARIABLES - Error when unable to get host") {
    val env = Map("other" -> "other", tokenEnvVariable -> testToken)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      getHostAndToken(useEnvVariables = true, "default", env)
    }
    assert(
      stream.toString.contains(
        s"ERROR: Unable to read $hostEnvVariable."
      )
    )
    stream.close()
  }

  test("ENVIRONMENT VARIABLES - Error when unable to get token") {
    val env = Map(hostEnvVariable -> testHost)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      getHostAndToken(useEnvVariables = true, "default", env)
    }
    assert(
      stream.toString.contains(
        s"ERROR: Unable to read $tokenEnvVariable."
      )
    )
    stream.close()
  }
}
