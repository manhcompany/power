package com.power.spark.utils

case class SparkConfiguration(
                             input: Option[SourceConfiguration] = None,
                             actions: Seq[ActionConfiguration] = Seq.empty,
                             output: Option[SinkConfiguration] = None
                             )

case class SourceConfiguration(path: Option[String], format: Option[String], options: Option[Seq[Opt]])

case class SinkConfiguration(path: Option[String], format: Option[String], options: Option[Seq[Opt]], mode: Option[String])

case class ActionConfiguration(
                              operator: String,
                              options: Option[Seq[Opt]],
                              select: Option[Seq[String]]
                              )

case class Opt(key: String, value: String)
