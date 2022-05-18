package com.databricks.partnerconnect.example.service

import com.databricks.partnerconnect.example.exceptions.AccountNotFoundException
import org.openapitools.client.model.ConnectionEnums.AccountStatus

import java.util.UUID
import scala.collection.mutable.ArrayBuffer

/** Test account service responsible for tracking customer account and users.
  */
class AccountService {
  var accounts: ArrayBuffer[AccountInfo] = ArrayBuffer()

  def getOrCreateAccount(domainName: String): AccountInfo = {
    var account = getAccountByName(domainName)
    if (account.isEmpty) {
      account = Some(AccountInfo(UUID.randomUUID().toString, domainName))
      accounts.append(account.get)
    }
    account.get
  }

  def addUser(accountId: String, email: String): Unit = {
    val account = getAccount(accountId).getOrElse(
      throw AccountNotFoundException(s"Account id ${accountId} not found")
    )
    account.addUser(email)
  }

  def getAccount(id: String): Option[AccountInfo] = {
    accounts.find(_.id == id)
  }

  def getAccountByName(name: String): Option[AccountInfo] = {
    accounts.find(_.name == name)
  }

  def getAccountByEmail(email: String): Option[AccountInfo] = {
    accounts.find(_.userExists(email))
  }

  def deleteAccount(id: String): Unit = {
    accounts = accounts.filter(_.id != id)
  }

  def expireAccount(id: String): Unit = {
    accounts = accounts.map(a =>
      if (a.id == id) AccountInfo(a.id, a.name, AccountStatus.Expired) else a
    )
  }
}
