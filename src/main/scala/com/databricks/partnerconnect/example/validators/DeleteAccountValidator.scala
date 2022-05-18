package com.databricks.partnerconnect.example.validators

import org.openapitools.client.model.{AccountInfo}

class DeleteAccountValidator extends Validator[AccountInfo] {
  private val deletableAccounts: Seq[String] =
    Seq("databricks-test.com", "databricks-demo.com")

  override def validate(request: AccountInfo): ValidationResult = {
    val isValid = request.domain.nonEmpty && deletableAccounts.contains(
      request.domain.toLowerCase
    )
    val invalidFields = if (isValid) Seq() else Seq("domain")
    ValidationResult(isValid, invalidFields)
  }
}
