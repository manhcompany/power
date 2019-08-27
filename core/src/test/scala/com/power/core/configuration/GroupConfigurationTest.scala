package com.power.core.configuration

import org.scalatest.FlatSpec

class GroupConfigurationTest extends FlatSpec {
  behavior of "GroupConfigurationTest"

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

  it should "explore" in {

  }

  it should "expand" in {
    assert(gA.expand() == eA3)
    assert(gB.expand() == eB2)
    assert(gC.expand() == eC2)
  }

  it should "buildDownStreams" in {
    val result = gA.buildDownStreams()
    assert(result.getDownStreams.length == 2)
  }

  it should "getAllElements" in {
    val result = gA.getAllElements
    assert(result.length == 3)
  }

  it should "get name" in {
    assert(gA.getName == "gA")
  }

  it should "equal" in {
    assert(gA.equals(gA))
    assert(!gA.equals(gB))
    assert(!gA.equals("abc"))
    assert(eB1.equals(eB1))
  }
}
