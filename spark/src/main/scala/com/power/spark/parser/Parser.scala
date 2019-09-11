package com.power.spark.parser

import com.power.core.graph.GraphContext
import com.power.core.graph.mutable.Graph
import com.power.spark.utils.{ActionConfiguration, Configuration, SinkConfiguration, SparkConfiguration}

object Parser {
  def buildGraph(sparkConfiguration: Map[String, SparkConfiguration]): Graph[Configuration] = {

    def buildDownStreams(prefix: String, configurations: Seq[(String, Configuration)]): List[GraphContext[Configuration]] = {
      configurations.reverse.tails.filter(x => x.size > 1).map(x => GraphContext(s"$prefix-${x.head._1}", List(), List(s"$prefix-${x.tail.head._1}"), x.head._2)).toList
    }

    def buildGraph(sparkConfiguration: Map[String, SparkConfiguration]): Graph[Configuration] = {
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
    }

    val graph = buildGraph(sparkConfiguration)

    val asTempTables = graph.filter(v => v.payLoad.isInstanceOf[ActionConfiguration]).filter(v => v.payLoad.asInstanceOf[ActionConfiguration].operator == "AS_TEMP_TABLE")
    asTempTables.foldLeft(graph)((g, s) => g.moveNodeToRoot(s.name))

    val sinkConfigurations = graph.filter(v => v.payLoad.isInstanceOf[SinkConfiguration])
    sinkConfigurations.foldLeft(graph)((g, s) => g.moveNodeToRoot(s.name))
  }

  def toPN(graph: Graph[Configuration]): Seq[Configuration] = {
    graph.toPNOrder.map(_.payLoad)
  }
}
