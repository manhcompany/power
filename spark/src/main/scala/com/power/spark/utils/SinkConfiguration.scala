package com.power.spark.utils

case class SinkConfiguration(
                              path: Option[String],
                              format: Option[String],
                              options: Option[Seq[Opt]],
                              mode: Option[String],
                              partitionBy: Option[List[String]],
                              username: Option[String],
                              password: Option[String],
                              url: Option[String],
                              table: Option[String]
                            ) extends Configuration {
  override def getOperatorName: String = "OUTPUT"
}