package com.power.spark.apps

import com.power.core.util.Logging
import com.power.spark.utils.Config

object ETL extends Logging {
  def main(args: Array[String]): Unit = {
    val config = Config.loadConfig("etl")
    
  }
}
