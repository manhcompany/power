package com.power.core.graph

import org.scalatest.FlatSpec

class VertexTest extends FlatSpec {

  behavior of "VertexTest"

  it should "equals" in {
    val vertex = new Vertex("v", List[Vertex[Int]](), 0)
    val vertex1 = new Vertex("v", List[Vertex[Int]](), 0)
    assert(vertex == vertex1)
  }

  it should "not equals" in {
    val vertex = new Vertex("v", List[Vertex[Int]](), 0)
    val vertex1 = new Vertex("v1", List[Vertex[Int]](), 0)
    assert(vertex != vertex1)
  }

  it should "not equals 1" in {
    val vertex = new Vertex("v", List[Vertex[Int]](), 0)
    val a = "v"
    assert(vertex != a)
  }

}
