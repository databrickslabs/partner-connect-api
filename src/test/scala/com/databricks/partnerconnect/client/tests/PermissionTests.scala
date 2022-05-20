package com.databricks.partnerconnect.client.tests

class PermissionTests extends PartnerTestBase {
  test(s"Validate permissions - ${configName}") {
    assert(
      config.data_permissions.isDefined || config.object_permissions.isDefined
    )

    if (config.object_permissions.isDefined) {
      config.object_permissions.get.foreach(p => {
        assert(p.name.toString.nonEmpty)
        assert(p.level.toString.nonEmpty)
      })
    }

    if (config.data_permissions.isDefined) {
      config.data_permissions.get.foreach(p => {
        assert(p.permission.toString.nonEmpty)
      })
    }
  }
}
