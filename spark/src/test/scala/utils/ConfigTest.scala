package utils

import org.scalatest.FlatSpec

class ConfigTest extends FlatSpec {

  behavior of "ConfigTest"

  it should "loadConfig" in {
    val a = Config.loadConfig("etl")
    assert(a.isInstanceOf[Map[String, SparkConfiguration]])

    assert(a("datasetA").isInstanceOf[SparkConfiguration])
    assert(a("datasetA").input.isInstanceOf[Some[SourceConfiguration]])
    assert(a("datasetA").output.isInstanceOf[Some[SinkConfiguration]])
    assert(a("datasetA").actions.isInstanceOf[Seq[ActionConfiguration]])

    assert(a("datasetB").isInstanceOf[SparkConfiguration])
    assert(a("datasetB").input.isInstanceOf[Some[SourceConfiguration]])
    assert(a("datasetB").output.isInstanceOf[Some[SinkConfiguration]])
    assert(a("datasetB").actions.isInstanceOf[Seq[ActionConfiguration]])
  }

  it should "loadConfig with operator" in {
    val config = Config.loadConfig("etl")
    assert(config("datasetA").actions.head.operator == "SELECT")
  }
}
