package com.power.spark.utils

case class MemoryOperatorConfiguration(operator: String, name: String) extends Configuration {
  override def getOperatorName: String = operator
}