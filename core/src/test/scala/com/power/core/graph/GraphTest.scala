package com.power.core.graph

import org.scalatest.FlatSpec

class GraphTest extends FlatSpec {

  behavior of "GraphTest"

  it should "explore" in {
    val v9 = new Vertex("v9", List[Vertex[Int]](), 0)
    val v10 = new Vertex("v10", List[Vertex[Int]](v9), 0)
    val v11 = new Vertex("v11", List[Vertex[Int]](v10), 0)
    val v5 = new Vertex("v5", List[Vertex[Int]](), 0)
    val v6 = new Vertex("v6", List[Vertex[Int]](v5), 0)
    val v7 = new Vertex("v7", List[Vertex[Int]](v6), 0)
    val v8 = new Vertex("v8", List[Vertex[Int]](v7), 0)
    val v12 = new Vertex("v12", List[Vertex[Int]](v8, v11), 0)
    val v13 = new Vertex("v13", List[Vertex[Int]](v12), 0)
    val v4 = new Vertex("v4", List[Vertex[Int]](), 0)
    val v3 = new Vertex("v3", List[Vertex[Int]](v4, v8), 0)
    val v2 = new Vertex("v2", List[Vertex[Int]](v3, v13), 0)
    val v1 = new Vertex("v1", List[Vertex[Int]](v2), 0)
    val result = Graph.explore(v1)
    val expectation = List[Vertex[Int]](v4, v5, v6, v7, v8, v3, v5, v6, v7, v8, v9, v10, v11, v12, v13, v2, v1)
    assert(result == expectation)
  }

  it should "has cycle is true" in {
    val v4 = new Vertex("v1", List[Vertex[Int]](), 0)
    val v3 = new Vertex("v3", List[Vertex[Int]](v4), 0)
    val v2 = new Vertex("v2", List[Vertex[Int]](v3), 0)
    val v1 = new Vertex("v1", List[Vertex[Int]](v2), 0)
    assert(Graph.hasCycle(v1))
  }

  it should "has cycle is false" in {
    val v3 = new Vertex("v3", List[Vertex[Int]](), 0)
    val v2 = new Vertex("v2", List[Vertex[Int]](v3), 0)
    val v1 = new Vertex("v1", List[Vertex[Int]](v2), 0)
    assert(!Graph.hasCycle(v1))
  }

}
