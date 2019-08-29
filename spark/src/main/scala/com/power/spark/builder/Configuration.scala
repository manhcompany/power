package com.power.spark.builder

trait Configuration extends ImpConfiguration {
  val name: String
  def getName: String
  def expand(): Configuration
  def getDownStreams: Seq[Configuration]
}
