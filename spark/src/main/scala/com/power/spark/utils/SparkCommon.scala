package com.power.spark.utils

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

object SparkCommon {
  def getSparkContext: SparkContext = {
    SparkContext.getOrCreate
  }

  var spark: SparkSession = _

  def getSparkSession(appName: String = "SparkApplicationFromTheHell"): SparkSession = {
    if (spark == null) {
      spark = SparkSession.builder().appName(appName).enableHiveSupport().getOrCreate()
    }
    spark
  }
}
