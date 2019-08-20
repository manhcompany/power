package com.power.core.zippers

import org.scalatest.FlatSpec

import scala.tools.nsc.doc.html.page.diagram.DotRunner

class TLPTest extends FlatSpec {
  class Foo {
    class Bar
  }

  class Bar

  it should "Test" in {
    val foo1 = new Foo
    val foo2 = new Foo

    val a: Foo#Bar = new foo1.Bar
    val b: Foo#Bar = new foo2.Bar

    val c: foo1.Bar = new foo1.Bar
  }

  it should "abstract type" in {
    trait Foo {
      type T
      def value: T
    }

    trait Bar {
      type T
    }

    object FooString extends Foo {
      type T = String

      override def value: T = "ciao"
    }

    object FooInt extends Foo {
      type T = Int

      override def value: T = 10
    }

    def getValue(f: Foo): f.T = f.value

    val fs: String = getValue(FooString)
    val fi: Int = getValue(FooInt)
  }

  it should "infix" in {
    object Foo {
      def bar(s: String): Unit = println(s)
    }

    Foo.bar("Hello")
    Foo bar "Hello"
  }

  it should "infix2" in {
    trait Foo[A, B]

    type Test1 = Foo[Int, String]
    type Test2 = Int Foo String
  }

  it should "infix3" in {
    trait ::[A, B]
    type Test1 = ::[Int, String]
    type Test2 = Int :: String
  }

  it should "Phantom type" in {
    trait Status
    trait Open extends Status
    trait Closed extends Status

    trait Door[S <: Status]

    object Door {
      def apply[S <: Status]: Door[S] = new Door[S] {

      }

      def open[S <: Closed](d: Door[S]): Door[Open] = Door[Open]
      def close[S <: Open](d: Door[S]): Door[Closed] = Door[Closed]
    }

    val closedDoor = Door[Closed]
    val openDoor = Door.open(closedDoor)
    val closedAgainDoor = Door.close(openDoor)

  }
}

class TLP1Test extends FlatSpec {


  it should "test" in {
    trait Foo {
      trait Bar

      def bar: Bar
    }

    def foo(f: Foo): f.Bar = f.bar
  }
}
