package com.power.spark.utils

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