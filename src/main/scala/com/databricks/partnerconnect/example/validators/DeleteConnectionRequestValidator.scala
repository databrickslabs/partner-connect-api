package com.databricks.partnerconnect.example.validators

import org.openapitools.client.model.DeleteConnectionRequest

class DeleteConnectionRequestValidator
    extends Validator[DeleteConnectionRequest] {
  override def validate(request: DeleteConnectionRequest): ValidationResult = {
    val isValid = request.connection_id.nonEmpty
    val invalidFields = if (isValid) Seq() else Seq("connection_id")
    ValidationResult(isValid, invalidFields)
  }
}
