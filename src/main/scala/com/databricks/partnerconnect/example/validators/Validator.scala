package com.databricks.partnerconnect.example.validators

case class ValidationResult(valid: Boolean, errors: Seq[String])

trait Validator[T] {
  def validate(request: T): ValidationResult
}
