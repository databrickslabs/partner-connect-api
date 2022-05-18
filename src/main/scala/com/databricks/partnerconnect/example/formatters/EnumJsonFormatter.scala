package com.databricks.partnerconnect.example.formatters

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

class EnumJsonFormatter[T <: scala.Enumeration](enum: T)
    extends RootJsonFormat[T#Value] {
  override def write(obj: T#Value): JsValue = JsString(obj.toString)

  override def read(json: JsValue): T#Value = {
    json match {
      case JsString(txt) => enum.withName(txt)
      case somethingElse =>
        throw DeserializationException(
          s"Unexpected value $somethingElse found for $enum"
        )
    }
  }
}
