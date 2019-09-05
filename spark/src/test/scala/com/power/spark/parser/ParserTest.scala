package com.power.spark.parser

import com.power.spark.utils.{Config, SinkConfiguration, SourceConfiguration, SparkConfiguration}
import org.scalatest.FlatSpec

class ParserTest extends FlatSpec {

  behavior of "ParserTest"

  it should "buildConfiguration" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    val graph = Parser.buildGraph(a)
    assert(graph.roots.size == 2)
    assert(graph.roots.map(_.name).equals(List("datasetA-2", "datasetB-1")))
    assert(graph.vertices("datasetA-1").downStreams.map(_.name).contains("datasetB-0"))
    assert(!graph.vertices("datasetA-1").downStreams.map(_.name).contains("datasetB-1"))
    assert(graph.vertices("datasetB-1").downStreams.map(_.name).contains("datasetB-0"))
//    assert(graph.roots.head.payLoad.isInstanceOf[SinkConfiguration])
//    assert(!graph.hasCycle)
//    assert(graph.toPNOrder.size == 5)
//    assert(graph.toPNOrder.reverse.head.payLoad.isInstanceOf[SinkConfiguration])
//    assert(graph.toPNOrder.head.payLoad.isInstanceOf[SourceConfiguration])
  }
}
