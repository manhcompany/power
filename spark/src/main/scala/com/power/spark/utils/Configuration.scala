package com.power.spark.utils

trait Configuration {
  def getDownStreams: Seq[String] = Seq[String]()

  def getOperatorName: String = "Empty"
}

case class Opt(key: String, value: String)

case class DescribeConfiguration(col: Option[String], summary: Seq[String])
