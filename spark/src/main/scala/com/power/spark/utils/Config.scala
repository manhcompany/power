package com.power.spark.utils

import com.typesafe.config.ConfigFactory

object Config {
  def loadConfig(namespace: String, fileName: Option[String] = None): Map[String, SparkConfiguration] = {
    val configuration = fileName match {
      case Some(x) => ConfigFactory.load(x)
      case None => ConfigFactory.load()
    }
    pureconfig.loadConfigOrThrow[Map[String, SparkConfiguration]](configuration, namespace)
  }
}
