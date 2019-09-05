package com.power.spark.utils

import com.typesafe.config.ConfigFactory

object Config {
  def loadConfig(namespace: String): Map[String, SparkConfiguration] = {
    val configuration = ConfigFactory.load()
    pureconfig.loadConfigOrThrow[Map[String, SparkConfiguration]](configuration, namespace)
  }
}
