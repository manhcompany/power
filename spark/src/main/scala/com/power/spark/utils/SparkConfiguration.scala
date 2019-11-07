package com.power.spark.utils

trait Configuration {
  def getDownStreams: Seq[String] = Seq[String]()

  def getOperatorName: String = "Empty"
}

case class SparkConfiguration(
                               input: Option[SourceConfiguration] = None,
                               actions: Seq[ActionConfiguration] = Seq.empty,
                               output: Option[SinkConfiguration] = None,
                               label: String = "main"
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
      case None => if (actions.nonEmpty) {
        actions.reverse.head
      } else input.get
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

case class SourceConfiguration(path: Option[String], format: Option[String], options: Option[Seq[Opt]], load: Option[String]) extends Configuration {
  override def getOperatorName: String = "INPUT"

  override def getDownStreams: Seq[String] = {
    load match {
      case Some(x) => Seq[String](x)
      case None => Seq[String]()
    }
  }
}

case class SinkConfiguration(
                              path: Option[String],
                              format: Option[String],
                              options: Option[Seq[Opt]],
                              mode: Option[String],
                              partitionBy: Option[List[String]]
                            ) extends Configuration {
  override def getOperatorName: String = "OUTPUT"
}

case class ActionConfiguration(
                                operator: String,
                                options: Option[Seq[Opt]],
                                exprs: Option[Seq[String]],
                                partitions: Option[Int],
                                tableName: Option[String],
                                sql: Option[String],
                                numberOfDatasets: Option[Int],
                                columns: Option[Seq[String]],
                                joinType: Option[String] = Some("inner"),
                                describes: Seq[DescribeConfiguration] = Seq.empty
                              ) extends Configuration {
  override def getDownStreams: Seq[String] = {
    options match {
      case Some(x) => x.filter(o => o.key == "OTHER_DATASETS").map(d => d.value)
      case None => Seq[String]()
    }
  }

  override def getOperatorName: String = operator
}

case class DescribeConfiguration(col: Option[String], summary: Seq[String])

case class Opt(key: String, value: String)

case class MemoryOperatorConfiguration(operator: String, name: String) extends Configuration {
  override def getOperatorName: String = operator
}
