package com.power.core.graphxxx

import com.power.core.configuration.{ElementConfiguration, GroupConfiguration}
import org.scalatest.FlatSpec

class ElementConfigurationTest extends FlatSpec {

  behavior of "ElementConfigurationTest"

  val eB1 = new ElementConfiguration(name = "eB1", dependencies = List(), downStreams = List())
  val eB2 = new ElementConfiguration(name = "eB2", dependencies = List(), downStreams = List(eB1))

  val eC2 = new ElementConfiguration(name = "eC2", dependencies = List(), downStreams = List())
  val eC1 = new ElementConfiguration(name = "eC1", dependencies = List(), downStreams = List(eC2))

  val gB = new GroupConfiguration(name = "gB", element = eB2, downStreams = List())
  val gC = new GroupConfiguration(name = "gC", element = eC2, downStreams = List())

  val eA1 = new ElementConfiguration(name = "eA1", dependencies = List(), downStreams = List())
  val eA2 = new ElementConfiguration(name = "eA2", dependencies = List(gB), downStreams = List(eA1))
  val eA3 = new ElementConfiguration(name = "eA3", dependencies = List(gC), downStreams = List(eA2))

  val gA = new GroupConfiguration(name = "gA", element = eA3, downStreams = List())

  it should "expand" in {
    assert(eA2.expand().getDownStreams.length == 2)
  }

  it should "explore" in {

  }

  it should "get name" in {
    assert(eB1.getName == "eB1")
  }

}
