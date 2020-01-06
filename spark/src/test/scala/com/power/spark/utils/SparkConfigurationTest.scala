package com.power.spark.utils

import org.apache.spark.sql.types.StringType
import org.scalatest.FlatSpec

class SparkConfigurationTest extends FlatSpec {
  it should "Opt" in {
    val opt = Opt("a", "b")
    assert(opt.key == "a")
    assert(opt.value == "b")
  }

  it should "size" in {
    val a = Config.loadConfig("test", Some("conf/application.conf"))
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").size == 5)
    assert(a("datasetB").size == 2)
    assert(a("datasetA").tail.isInstanceOf[SinkConfiguration])
  }

  it should "tail" in {
    val a = Config.loadConfig("test", Some("conf/application.conf"))
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").tail.isInstanceOf[SinkConfiguration])
  }

  it should "toList" in {
    val a = Config.loadConfig("test", Some("conf/application.conf"))
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").toList.head._1 == "0")
    assert(a("datasetA").toList.size == 5)
    assert(a("datasetA").toList.head._2.isInstanceOf[SourceConfiguration])
    assert(a("datasetA").toList.reverse.head._2.isInstanceOf[SinkConfiguration])
  }

  it should "options" in {
    val a = Config.loadConfig("test", Some("conf/application.conf"))
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").actions.head.options.get.head.key == "OTHER_DATASETS")
    assert(a("datasetA").actions.head.options.get.head.value == "datasetB")
  }

  it should "getDownStreams" in {
    val a = Config.loadConfig("test", Some("conf/application.conf"))
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])
    assert(a("datasetA").actions.head.getDownStreams.size == 1)
    assert(a("datasetA").actions.head.getDownStreams.head == "datasetB")
  }

  it should "label" in {
    val a = Config.loadConfig("test", Some("conf/application.conf"))
    StringType
    assert(a.head._2.label == "main")
  }
}
