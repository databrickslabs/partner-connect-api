package com.databricks.partnerconnect.example.validators

import org.openapitools.client.model.{AccountUserInfo}

class ExpireAccountValidator extends Validator[AccountUserInfo] {
  private val accounts: Seq[String] =
    Seq("databricks-test.com", "databricks-demo.com")

  override def validate(request: AccountUserInfo): ValidationResult = {
    val isValid = request.email.nonEmpty && accounts.exists(
      request.email.toLowerCase.endsWith(_)
    )
    val invalidFields = if (isValid) Seq() else Seq("email")
    ValidationResult(isValid, invalidFields)
  }
}
