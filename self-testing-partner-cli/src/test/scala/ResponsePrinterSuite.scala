import com.databricks.labs.{Create, Delete, Get, Update}
import org.scalatest.funsuite.AnyFunSuite
import requests.Response

import scala.collection.mutable
import com.databricks.labs.ResponsePrinter.printResponse

import java.io.FileNotFoundException
import scala.util.{Failure, Success}

class ResponsePrinterSuite extends AnyFunSuite {
  val host = "localhost"
  val uuid = "01EDC356A4631F10AC4AE0120A591336"
  val name = "Test Partner"
  test("Create - Success with status code 200 prints success message") {
    val response = Success(
      Response(
        url = host,
        statusCode = 200,
        statusMessage = "Success",
        headers = Map("content-type" -> mutable.Buffer("application/json")),
        data = new geny.Bytes(s"""{"id": "$uuid"}""".getBytes),
        history = None
      )
    )

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Create())
    }
    val output = stream.toString()
    assert(
      output.contains(
        s"SUCCESS: Successfully created self-testing partner with UUID: $uuid"
      )
    )
    assert(output.contains("Go to your workspace to see the test tile."))
    stream.close()
  }

  test("Get - Success with status code 200 prints partners") {
    val response = Success(
      Response(
        url = host,
        statusCode = 200,
        statusMessage = "Success",
        headers = Map("content-type" -> mutable.Buffer("application/json")),
        data = new geny.Bytes(
          s"""{"partners": [{"id": "$uuid", "name": "$name"}]}""".getBytes
        ),
        history = None
      )
    )

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Get())
    }
    val output = stream.toString()
    assert(
      output.contains("Self-Testing Partners:")
    )
    assert(output.contains(s"""1. ID: "$uuid"\tName: "$name""""))
    stream.close()
  }

  test(
    "Get - Success with status code 200 when there are no self-testing partners"
  ) {
    val response = Success(
      Response(
        url = host,
        statusCode = 200,
        statusMessage = "Success",
        headers = Map("content-type" -> mutable.Buffer("application/json")),
        data = new geny.Bytes("{}".getBytes),
        history = None
      )
    )

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Get())
    }
    val output = stream.toString()
    assert(output.contains("No active self-testing partners were found."))
    stream.close()
  }

  test("Update - Success with status code 200 prints success message") {
    val response = Success(
      Response(
        url = host,
        statusCode = 200,
        statusMessage = "Success",
        headers = Map("content-type" -> mutable.Buffer("application/json")),
        data = new geny.Bytes(s"{}".getBytes),
        history = None
      )
    )

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Update())
    }
    val output = stream.toString()
    assert(
      output.contains("SUCCESS: Successfully updated the self-testing partner.")
    )
    assert(output.contains("Go to your workspace to see the changes."))
    stream.close()
  }

  test("Delete - Success with status code 200 prints success message") {
    val response = Success(
      Response(
        url = host,
        statusCode = 200,
        statusMessage = "Success",
        headers = Map("content-type" -> mutable.Buffer("application/json")),
        data = new geny.Bytes(s"{}".getBytes),
        history = None
      )
    )

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Delete())
    }
    val output = stream.toString()
    assert(
      output.contains("SUCCESS: Successfully deleted the self-testing partner.")
    )
    assert(output.contains("Go to your workspace to see the changes."))
    stream.close()
  }

  test("Success with error code prints error message - json") {
    val message = "Workspace already has an active self-testing partner."
    val response = Success(
      Response(
        url = host,
        statusCode = 400,
        statusMessage = "Bad Request",
        headers = Map("content-type" -> mutable.Buffer("application/json")),
        data = new geny.Bytes(
          s"""{"error_code":"BAD_REQUEST","message":"$message"}""".getBytes
        ),
        history = None
      )
    )

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Create())
    }
    val output = stream.toString()
    assert(
      output.contains(s"ERROR: Could not create self testing partner.")
    )
    assert(
      output.contains(s"Received status code 400 with message: $message")
    )
    stream.close()
  }

  test(
    "Success with error code prints error message - x-databricks-reason-phrase"
  ) {
    val reason = "Invalid access token."
    val response = Success(
      Response(
        url = host,
        statusCode = 403,
        statusMessage = "Forbidden",
        headers = Map(
          "content-type" -> mutable.Buffer("text/html"),
          "x-databricks-reason-phrase" -> mutable.Buffer(reason)
        ),
        data = new geny.Bytes(
          s"""<html></html>""".getBytes
        ),
        history = None
      )
    )

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Create())
    }
    val output = stream.toString()
    assert(
      output.contains(s"ERROR: Could not create self testing partner.")
    )
    assert(
      output.contains(s"Received status code 403 with message: $reason")
    )
    stream.close()
  }

  test(
    "Success with error code prints error message - other"
  ) {
    val statusMessage = "status message"
    val response = Success(
      Response(
        url = host,
        statusCode = 400,
        statusMessage = statusMessage,
        headers = Map(
          "content-type" -> mutable.Buffer("text/html")
        ),
        data = new geny.Bytes(
          s"""<html></html>""".getBytes
        ),
        history = None
      )
    )

    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Create())
    }
    val output = stream.toString()
    assert(
      output.contains(s"ERROR: Could not create self testing partner.")
    )
    assert(
      output.contains(
        s"Received status code 400 with message: $statusMessage"
      )
    )
    stream.close()
  }

  test("Failure from exception prints exception message") {
    val exception =
      new FileNotFoundException("~/.databrickscfg (No such file or directory)")
    val response = Failure(exception)
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      printResponse(response, Create())
    }
    val output = stream.toString()
    assert(
      output.contains(
        s"ERROR: Received ${exception.getClass.getSimpleName} with message: ${exception.getMessage}"
      )
    )
  }
}
