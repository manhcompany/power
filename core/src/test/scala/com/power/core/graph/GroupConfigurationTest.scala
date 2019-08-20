package com.power.core.graph

import org.scalatest.FlatSpec

class GroupConfigurationTest extends FlatSpec {

  behavior of "GroupConfigurationTest"

  it should "explore" in {
    val g5 = new GroupConfiguration(List(), List())
    val g4 = new GroupConfiguration(List(), List(g5))
    val g3 = new GroupConfiguration(List(), List())
    val g2 = new GroupConfiguration(List(), List(g3, g4))
    val g1 = new GroupConfiguration(List(), List(g2))
    val result = g1.explore()
    assert(result == List(g3, g5, g4, g2, g1))
  }

  it should "expand" in {
    val endElement = new ElementConfiguration(List(), List())
    val first = new ElementConfiguration(List(), List(endElement))
    val elementConfiguration = new ElementConfiguration(List(new GroupConfiguration(List(), List())), List(first))
    val groupConfiguration = new GroupConfiguration(List(elementConfiguration), List())
    val result = groupConfiguration.expand()
    assert(endElement == result)
  }
}
