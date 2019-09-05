package com.power.spark.parser

import com.power.core.graph.GraphContext
import com.power.core.graph.mutable.Graph
import com.power.spark.utils.{Configuration, SparkConfiguration}

object Parser {
  def buildConfiguration(sparkConfiguration: Map[String, SparkConfiguration]): Graph[Configuration] = {

    def buildDownStreams(prefix: String, configurations: Seq[(String, Configuration)]): List[GraphContext[Configuration]] = {
      configurations.reverse.tails.filter(x => x.size > 1).map(x => GraphContext(s"$prefix-${x.head._1}", List(), List(s"$prefix-${x.tail.head._1}"), x.head._2)).toList
    }

    val context = sparkConfiguration.foldLeft(List[GraphContext[Configuration]]())((r, x) => {
      val prefix = x._1
      x._2.toList.foldLeft(r)((rr, xx) => {
        val name = s"$prefix-${xx._1}"
        val downStreams = xx._2.getUpStreams.map(u => s"$u-${sparkConfiguration(u).size - 1}")
        GraphContext(name, List(), downStreams, xx._2) :: rr
      }) ::: {
        buildDownStreams(prefix, x._2.toList)
      } ::: r
    })
    val graph = Graph(context.reverse)
    graph.build()
    graph
  }
}
