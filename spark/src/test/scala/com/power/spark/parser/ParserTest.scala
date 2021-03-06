package com.power.spark.parser

import com.power.core.engine.stackmachine.{CanonicalStackMachine, Operator, OperatorAdapter, StackOperator}
import com.power.spark.utils.{Config, SinkConfiguration, SourceConfiguration, SparkConfiguration, SparkOperatorFactory}
import org.apache.spark.sql.DataFrame
import org.scalatest.FlatSpec

class ParserTest extends FlatSpec {

  behavior of "ParserTest"

  it should "buildConfiguration" in {
    val a = Config.loadConfig("test" ,Some("conf/application.conf"))
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    val graph = Parser.buildGraph(a)
    assert(graph.roots.size == 2)
    assert(graph.roots.map(_.name).contains("datasetA-4"))
    assert(graph.roots.map(_.name).contains("datasetB-1"))
    assert(graph.vertices("datasetA-1").downStreams.head.name.equals("datasetA-0"))
    assert(graph.vertices("datasetA-2").downStreams.head.name.equals("datasetA-1"))
    assert(graph.vertices("datasetA-3").downStreams.head.name.equals("datasetA-2"))
    assert(graph.vertices("datasetA-4").downStreams.head.name.equals("datasetA-3"))
    assert(!graph.vertices("datasetA-1").downStreams.map(_.name).contains("datasetB-1"))
  }

  it should "toPN" in {
    val a = Config.loadConfig("test", Some("conf/application.conf"))
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    val graph = Parser.buildGraph(a)
    val PNOrder = Parser.toPN(graph)
    assert(PNOrder.size == 12)
    assert(PNOrder.reverse.head.isInstanceOf[SinkConfiguration])
    assert(PNOrder.head.isInstanceOf[SourceConfiguration])
    println(PNOrder.map(_.getOperatorName))
    assert(PNOrder.map(_.getOperatorName).toList.equals(List("INPUT", "INPUT", "STORE", "LOAD", "UNION", "SELECT", "REPARTITION", "OUTPUT", "INPUT", "STORE", "LOAD", "OUTPUT")))
  }

  it should "toOptimizedPN" in {
    val a = Config.loadConfig("test", Some("conf/application.conf"))
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    val graph = Parser.buildGraph(a)
    val PNOrder = Parser.toOptimizedPN(graph)
    assert(PNOrder.size == 10)
    assert(PNOrder.reverse.head.isInstanceOf[SinkConfiguration])
    assert(PNOrder.head.isInstanceOf[SourceConfiguration])
    println(PNOrder.map(_.getOperatorName))
    assert(PNOrder.map(_.getOperatorName).toList.equals(List("INPUT", "INPUT", "STORE", "LOAD", "UNION", "SELECT", "REPARTITION", "OUTPUT", "LOAD", "OUTPUT")))
  }
}
