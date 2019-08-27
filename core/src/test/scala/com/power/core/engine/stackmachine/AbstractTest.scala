package com.power.core.engine.stackmachine

import org.scalatest.FlatSpec

class AbstractTest extends FlatSpec{
  class A {
    def hello(): Unit = {
      println("Hello")
    }
  }

  trait B extends A

  class C extends B

  it should "trait" in {
    val c = new C()
    c.hello()
  }
}
