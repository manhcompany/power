package com.power.spark.parser

import com.power.core.graph.GraphContext
import com.power.core.graph.mutable.Graph
import com.power.spark.utils.{Configuration, MemoryOperatorConfiguration, SinkConfiguration, SparkConfiguration}

object Parser {
  def buildGraph(sparkConfiguration: Map[String, SparkConfiguration]): Graph[Configuration] = {

    def buildDownStreams(prefix: String, configurations: Seq[(String, Configuration)]): List[GraphContext[Configuration]] = {
      (
        configurations.reverse.tails.filter(x => x.size > 1).map(x => GraphContext(s"$prefix-${x.head._1}", List(), List(s"$prefix-${x.tail.head._1}"), x.head._2)) ++
        configurations.reverse.tails.filter(x => x.size == 1).map(x => GraphContext(s"$prefix-${x.head._1}", List(), List(), x.head._2))
      ).toList
    }

    def buildGraph(sparkConfiguration: Map[String, SparkConfiguration]): Graph[Configuration] = {
      def buildDatasets(): Graph[Configuration] = {
        val context = sparkConfiguration.foldLeft(List[GraphContext[Configuration]]())((r, x) =>{
          val prefix = x._1
          buildDownStreams(prefix, x._2.toList) ::: r
        })

        val graph = Graph(context)
        graph.build()
      }

      def downStreams(graph: Graph[Configuration]): Graph[Configuration] = {
        val edges = sparkConfiguration.foldLeft(List[(String, String)]())((r, x) => {
          val prefix = x._1
          x._2.toList.foldLeft(r)((rr, xx) => {
            val name = s"$prefix-${xx._1}"
            val downStreams = xx._2.getDownStreams.map(u => s"$u-${sparkConfiguration(u).size - 1}")
            downStreams.foldLeft(rr)((rrr, xxx) => {
              (name, xxx) :: rrr
            })
          })
        })

        edges.foldLeft(graph)((g, e) => g.addEdge(e._1, e._2))
      }
      val graph = buildDatasets()
      downStreams(graph)
    }

    val graph = buildGraph(sparkConfiguration)

    val sinkConfigurations = graph.filter(v => v.payLoad.isInstanceOf[SinkConfiguration])
    sinkConfigurations.foldLeft(graph)((g, s) => g.moveNodeToRoot(s.name))

    val overlaps = graph.filter(x => x.upStreams.size > 1)
    overlaps.foreach(v => {
      val variableName = (scala.util.Random.alphanumeric take 10).foldLeft("")((r, c) => s"$r$c")
      val storeConfig = MemoryOperatorConfiguration(operator = "STORE", name = variableName)
      val storeVertexName: String = s"${v.name}-STORE-$variableName"
      graph.addContext(GraphContext(storeVertexName, List(), List(), storeConfig))
      v.upStreams.foreach(u => {
        val loadConfig = MemoryOperatorConfiguration(operator = "LOAD", name = variableName)
        graph.addContext(GraphContext(s"${u.name}-LOAD-$variableName", List(u.name), List(storeVertexName), loadConfig))
        graph.removeEdge(u.name, v.name)
      })
      graph.addEdge(storeVertexName, v.name)
    })
    graph
  }

  def toPN(graph: Graph[Configuration]): Seq[Configuration] = {
    graph.toPNOrder.map(_.payLoad)
  }

  def toOptimizedPN(graph: Graph[Configuration]): Seq[Configuration] = {
    graph.toPNOrderOptimize.map(_.payLoad)
  }
}
