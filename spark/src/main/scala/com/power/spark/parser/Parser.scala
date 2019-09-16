package com.power.spark.parser

import com.power.core.graph.{GraphContext, Vertex}
import com.power.core.graph.mutable.Graph
import com.power.spark.utils.{ActionConfiguration, Configuration, SinkConfiguration, SparkConfiguration}

import scala.collection.mutable.ListBuffer

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

    // TODO move to Graph class
    val vertexHasMultiParents = graph.vertexHasMultiParents
    val proxyVertices = vertexHasMultiParents.foldLeft(graph)((g, v) => {
      val proxyConfiguration = ActionConfiguration(operator = "PROXY", aliasName = Some(s"proxy-${v.getName}"))
      val proxyVertex = Vertex[Configuration](s"proxy-${v.getName}", ListBuffer(), ListBuffer(), proxyConfiguration)
      g.addVertex(proxyVertex)
      val upStreams = v.upStreams.clone()
      val downStreams = v.downStreams
      val addedEdges = (proxyVertex.getName, v.getName) +: upStreams.foldLeft(Seq[(String, String)]())((edges, u) =>{
        (u.getName, proxyVertex.getName) +: edges
      })
      addedEdges.foldLeft(g)((gg, e) => gg.addEdge(e._1, e._2))

      val removedEdges = upStreams.foldLeft(Seq[(String, String)]())((edges, u) => {
        (u.getName, v.getName) +: edges
      })
      removedEdges.foldLeft(g)((gg, e) => gg.removeEdge(e._1, e._2))
    })
    graph
  }

  def toPN(graph: Graph[Configuration]): Seq[Configuration] = {
    graph.toPNOrder.map(_.payLoad)
  }
}
