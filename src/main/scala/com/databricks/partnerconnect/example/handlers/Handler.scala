package com.databricks.partnerconnect.example.handlers

import akka.http.scaladsl.server.StandardRoute

trait Handler[T] {
  def handle(request: T): StandardRoute = ???
  def handle(): StandardRoute = ???
}
