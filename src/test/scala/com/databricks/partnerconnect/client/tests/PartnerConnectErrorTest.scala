package com.databricks.partnerconnect.client.tests

import org.openapitools.client.core.ApiError
import org.openapitools.client.model.ErrorResponseEnums.ErrorReason

class PartnerConnectErrorTest extends PartnerTestBase {

  test(s"Invalid auth header throws 401. - ${configName}") {
    val request = createConnectRequest(
      false,
      "user1@company3.com",
      12345678923441L,
      12345678923441L,
      userName = Some("badUser"),
      password = Some("badPassword")
    )
    val caught = intercept[ApiError[String]] {
      executeConnectRequest(request)
    }
    validateError(caught, 401, ErrorReason.Unauthorized)
  }
}
