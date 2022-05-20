package com.databricks.partnerconnect.example.exceptions

case class AccountNotFoundException(message: String) extends Exception(message)
