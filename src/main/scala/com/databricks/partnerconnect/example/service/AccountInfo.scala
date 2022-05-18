package com.databricks.partnerconnect.example.service

import org.openapitools.client.model.ConnectionEnums.AccountStatus

import scala.collection.mutable.Set

/** Class representing a customer account
  */
case class AccountInfo(
    id: String,
    name: String,
    status: AccountStatus = AccountStatus.Active
) {
  private val users: Set[String] = Set()

  def addUser(email: String): Unit = {
    users.add(email)
  }
  def userExists(email: String): Boolean = {
    users.contains(email)
  }
}
