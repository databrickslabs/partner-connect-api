package com.databricks.labs

trait CommandType {
  val name: String
}

case class Create() extends CommandType {
  val name = "create"
}

case class Get() extends CommandType {
  val name = "get"
}

case class Update() extends CommandType {
  val name = "update"
}

case class Delete() extends CommandType {
  val name = "delete"
}
