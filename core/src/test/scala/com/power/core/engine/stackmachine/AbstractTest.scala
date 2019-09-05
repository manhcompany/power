package com.power.core.engine.stackmachine

import org.scalatest.FlatSpec

class AbstractTest extends FlatSpec{

  trait B {
    def hello() = {
      println("Hello")
    }
  }


  case class C(name: String) extends B

  it should "trait" in {
    val c = C("ab")
    c.hello()
  }
}
