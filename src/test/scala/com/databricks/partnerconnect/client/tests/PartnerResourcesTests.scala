package com.databricks.partnerconnect.client.tests

import org.openapitools.client.model.ConnectionEnums.RedirectValue
import org.openapitools.client.model.ResourceToProvisionEnums.ResourceType.InteractiveCluster

class PartnerResourcesTests extends PartnerTestBase {

  test(s"interactive cluster connection test - ${configName}") {
    assume(
      config.resources_to_provision.exists(
        _.resource_type == InteractiveCluster
      )
    )
    val request = createConnectRequest(
      false,
      "user1@PartnerResourcesTests.com",
      12345678923441L,
      12345678923441L
    )
    val res = executeConnectRequest(request)
    validateNewConnection(res)
  }
}
