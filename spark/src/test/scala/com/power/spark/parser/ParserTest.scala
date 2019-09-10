package com.power.spark.parser

import com.power.core.engine.stackmachine.{CanonicalStackMachine, Operator, OperatorAdapter, StackOperator}
import com.power.spark.utils.{Config, SinkConfiguration, SourceConfiguration, SparkConfiguration, SparkOperatorFactory}
import org.apache.spark.sql.DataFrame
import org.scalatest.FlatSpec

class ParserTest extends FlatSpec {

  behavior of "ParserTest"

  it should "buildConfiguration" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    val graph = Parser.buildGraph(a)
    assert(graph.roots.size == 2)
    assert(graph.roots.map(_.name).contains("datasetA-4"))
    assert(graph.roots.map(_.name).contains("datasetB-1"))
    assert(graph.vertices("datasetA-1").downStreams.map(_.name).contains("datasetB-0"))
    assert(!graph.vertices("datasetA-1").downStreams.map(_.name).contains("datasetB-1"))
    assert(graph.vertices("datasetB-1").downStreams.map(_.name).contains("datasetB-0"))
  }

  it should "toPN" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    val graph = Parser.buildGraph(a)
    val PNOrder = Parser.toPN(graph)
    assert(PNOrder.size == 8)
    assert(PNOrder.reverse.head.isInstanceOf[SinkConfiguration])
    assert(PNOrder.head.isInstanceOf[SourceConfiguration])
  }
}
