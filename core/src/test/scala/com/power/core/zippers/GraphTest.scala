package com.power.core.zippers

import org.scalatest.FlatSpec

class GraphTest extends FlatSpec {
  type Node[A] = A

  type Adj[A] = List[(String, A)]

  type Context[A] = (Adj[A], Node[A], String, Adj[A])

  sealed trait Graph[A]

  case class Empty[A]() extends Graph[A]

  case class Vertex[A](context: Context[A], graph: Graph[A]) extends Graph[A]

  val g: Vertex[Int] = Vertex[Int](context = (List(), 1, "a", List())
    , graph = Empty[Int]())

  val g1: Vertex[Int] = Vertex[Int](context = (
    List(("12", 1)),
    2, "b",
    List(("21", 1))
  ), graph = g)

  val g2: Vertex[Int] = Vertex[Int](context = (
    List(("23", 2)),
    3, "c",
    List(("31", 1))
  ), graph = g1)

  println(g2)
}
