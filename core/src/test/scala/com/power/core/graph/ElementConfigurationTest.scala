package com.power.core.graph

import org.scalatest.FlatSpec

class ElementConfigurationTest extends FlatSpec {

  behavior of "ElementConfigurationTest"

  it should "expand" in {
    val endElement = new ElementConfiguration(List(), List())
    val elementConfiguration = new ElementConfiguration(List(), List(endElement))
    val result = elementConfiguration.expand()
    println(result)
  }

  it should "explore" in {
    val element = new ElementConfiguration(List(), List())
    val element1 = new ElementConfiguration(List(), List(element))
    val element2 = new ElementConfiguration(List(), List(element1))
    val element4 = new ElementConfiguration(List(), List())
    val element3 = new ElementConfiguration(List(), List(element2, element4))
    val result = element3 explore()
    val expectation = List(element, element1, element2, element4, element3)
    assert(expectation == result)
  }

}
