package com.databricks.partnerconnect.example.validators

import org.openapitools.client.model.ConnectionInfo

class ConnectionInfoValidator extends Validator[ConnectionInfo] {
  override def validate(request: ConnectionInfo): ValidationResult = {
    val isValid = request.connection_id.nonEmpty
    val invalidFields = if (isValid) Seq() else Seq("connection_id")
    ValidationResult(isValid, invalidFields)
  }
}
