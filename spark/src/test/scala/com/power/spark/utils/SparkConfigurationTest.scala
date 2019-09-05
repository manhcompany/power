package com.power.spark.utils

import org.scalatest.FlatSpec

class SparkConfigurationTest extends FlatSpec {
  it should "Opt" in {
    val opt = Opt("a", "b")
    assert(opt.key == "a")
    assert(opt.value == "b")
  }

  it should "size" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").size == 3)
    assert(a("datasetB").size == 2)
    assert(a("datasetA").tail.isInstanceOf[SinkConfiguration])
  }

  it should "tail" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").tail.isInstanceOf[SinkConfiguration])
  }

  it should "toList" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").toList.head._1 == "0")
    assert(a("datasetA").toList.size == 3)
    assert(a("datasetA").toList.head._2.isInstanceOf[SourceConfiguration])
    assert(a("datasetA").toList.reverse.head._2.isInstanceOf[SinkConfiguration])
  }

  it should "options" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").actions.head.options.get.head.key == "OTHER_DATASETS")
    assert(a("datasetA").actions.head.options.get.head.value == "datasetB")
  }

  it should "getUpStreams" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").actions.head.getUpStreams.size == 1)
    assert(a("datasetA").actions.head.getUpStreams.head == "datasetB")
  }
}
