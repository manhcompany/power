package com.power.spark.utils

trait Configuration {
  def getUpStreams: Seq[String] = Seq[String]()
}

case class SparkConfiguration(
                             input: Option[SourceConfiguration] = None,
                             actions: Seq[ActionConfiguration] = Seq.empty,
                             output: Option[SinkConfiguration] = None
                             ) {
  val size: Int = {
    actions.size + (input match {
      case Some(_) => 1
      case None => 0
    }) + (output match {
      case Some(_) => 1
      case None => 0
    })
  }

  val tail: Configuration = {
    output match {
      case Some(x) => x
      case None => if(actions.nonEmpty) { actions.reverse.head } else input.get
    }
  }

  val toList: List[(String, Configuration)] = {
    val configs = (input match {
      case Some(x) => List(x)
      case None => List()
    }) ++ actions ++ (output match {
      case Some(x) => List(x)
      case None => List()
    })
    configs.zipWithIndex.foldLeft(List[(String, Configuration)]())((r, x) => (x._2.toString, x._1) :: r).reverse
  }
}

case class SourceConfiguration(path: Option[String], format: Option[String], options: Option[Seq[Opt]]) extends Configuration

case class SinkConfiguration(path: Option[String], format: Option[String], options: Option[Seq[Opt]], mode: Option[String]) extends Configuration

case class ActionConfiguration(
                              operator: String,
                              options: Option[Seq[Opt]],
                              select: Option[Seq[String]]
                              ) extends Configuration {
  override def getUpStreams: Seq[String] = {
    options match {
      case Some(x) => x.filter(o => o.key == "OTHER_DATASETS").map(d => d.value)
      case None => Seq[String]()
    }
  }
}

case class Opt(key: String, value: String)
