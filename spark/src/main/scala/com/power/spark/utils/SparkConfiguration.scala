package com.power.spark.utils


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

