package com.power.spark.utils

import org.scalatest.FlatSpec

class SparkConfigurationTest extends FlatSpec {
  it should "Opt" in {
    val opt = Opt("a", "b")
    assert(opt.key == "a")
    assert(opt.value == "b")
  }
}
