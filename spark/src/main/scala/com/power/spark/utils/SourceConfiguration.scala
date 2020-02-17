package com.power.spark.utils

case class SourceConfiguration(path: Option[String], format: Option[String], options: Option[Seq[Opt]], load: Option[String]) extends Configuration {
  override def getOperatorName: String = "INPUT"

  override def getDownStreams: Seq[String] = {
    load match {
      case Some(x) => Seq[String](x)
      case None => Seq[String]()
    }
  }
}
