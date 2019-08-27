package com.power.core.zippers

import org.scalatest.FlatSpec

class TypeClassTest extends FlatSpec {
  trait Show[A] {
    def show(a: A): String
  }

  object Show {
    def apply[A](implicit sh: Show[A]): Show[A] = sh

    object ops {
      def show[A: Show](a: A): String = Show[A].show(a)

      implicit class ShowOps[A: Show](a: A) {
        def show: String = Show[A].show(a)
      }
    }
  }

  implicit val intCanShow: Show[Int] = int => s"int $int"

  implicit val stringCanShow: Show[String] = str => s"str $str"

  implicit val doubleCanShow: Show[Double] = db => s"double $db"

  case class Foo(foo: Int)

  implicit val fooShow: Show[Foo] = foo => s"case class Foo(foo: ${foo.foo})"


  it should "show" in {
//    println(Show.intCanShow.show(20))
//    println(Show.show(20))
    import Show.ops.ShowOps
    println(20 show)
    println("hello" show)
    println(0.001 show)
    println(Foo(40) show)
  }
}
