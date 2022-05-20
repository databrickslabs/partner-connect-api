package com.databricks.partnerconnect.client.tests

import com.databricks.partnerconnect.example.util.PartnerConfigUtil

class RepoSyncTest extends PartnerTestBase {
  test("Validate prod configs are not synced to public repo.") {
    val isPublic =
      sys.env.contains("IS_REPO_PUBLIC") && sys.env("IS_REPO_PUBLIC").toBoolean
    assume(isPublic)
    assert(
      !PartnerConfigUtil.getProdConfigExists(),
      "Prod config should not be in public repo."
    )
  }
}
