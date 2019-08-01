package com.power.core.graph

import org.scalatest.FlatSpec

class GraphTest extends FlatSpec {

  behavior of "GraphTest"

  it should "explore" in {
    val v9 = new Vertex("v9", List[Vertex]())
    val v10 = new Vertex("v10", List[Vertex](v9))
    val v11 = new Vertex("v11", List[Vertex](v10))
    val v5 = new Vertex("v5", List[Vertex]())
    val v6 = new Vertex("v6", List[Vertex](v5))
    val v7 = new Vertex("v7", List[Vertex](v6))
    val v8 = new Vertex("v8", List[Vertex](v7))
    val v12 = new Vertex("v12", List[Vertex](v8, v11))
    val v13 = new Vertex("v13", List[Vertex](v12))
    val v4 = new Vertex("v4", List[Vertex]())
    val v3 = new Vertex("v3", List[Vertex](v4, v8))
    val v2 = new Vertex("v2", List[Vertex](v3, v13))
    val v1 = new Vertex("v1", List[Vertex](v2))
    val result = Graph.explore(v1)
    result.foreach(v => println(v.getName))
    val expectation = List[Vertex](v4, v5, v6, v7, v8, v3, v5, v6, v7, v8, v9, v10, v11, v12, v13, v2, v1)
    assert(result == expectation)
  }

}
