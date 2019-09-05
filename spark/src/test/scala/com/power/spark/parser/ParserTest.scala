package com.power.spark.parser

import com.power.spark.utils.{Config, SinkConfiguration, SourceConfiguration, SparkConfiguration}
import org.scalatest.FlatSpec

class ParserTest extends FlatSpec {

  behavior of "ParserTest"

  it should "buildConfiguration" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    val graph = Parser.buildConfiguration(a)
    assert(graph.roots.size == 1)
    assert(graph.roots.head.payLoad.isInstanceOf[SinkConfiguration])
    assert(!graph.hasCycle)
    assert(graph.toPNOrder.size == 5)
    assert(graph.toPNOrder.reverse.head.payLoad.isInstanceOf[SinkConfiguration])
    assert(graph.toPNOrder.head.payLoad.isInstanceOf[SourceConfiguration])
  }
}
