package com.power.core.graph

import com.power.core.graph.mutable.Graph
import org.scalatest.FlatSpec

import scala.collection.mutable.ListBuffer

class GraphTest extends FlatSpec {

  behavior of "GraphTest"

  val configs: List[GraphContext[Int]] = List[GraphContext[Int]](
    GraphContext("a", List(), List("b"), 1),
    GraphContext("b", List("a", "c"), List(), 1),
    GraphContext("c", List(), List("b"), 1)
  )

  it should "create_vertices" in {
    val graph = Graph(configs)
    graph.createVertices()
    assert(graph.getVertices.size == 3)
  }

  it should "create edges" in {
    val graph = Graph(configs)
    graph.createVertices()
    graph.createEdges()
    assert(graph.vertices("a").downStreams.size == 1)
    assert(graph.vertices("b").upStreams.size == 2)
    assert(graph.vertices("c").downStreams.size == 1)
  }

  it should "build" in {
    val graph = Graph(configs)
    graph.build()
    assert(graph.vertices("a").downStreams.size == 1)
    assert(graph.vertices("b").upStreams.size == 2)
    assert(graph.vertices("c").downStreams.size == 1)
    assert(graph.getVertices.size == 3)
  }

  it should "roots" in {


    val graph = Graph(configs)
    graph.build()
    assert(graph.roots.length == 2)
  }

  it should "removeEdge" in {
    val graph = Graph(configs)
    graph.build()
    graph.removeEdge("a", "b")
    assert(graph.vertices("a").downStreams.isEmpty)
    assert(graph.vertices("b").upStreams.size == 1)
  }

  it should "removeVertex" in {


    val graph = Graph(configs)
    graph.build()
    graph.removeVertex("a")
    assert(!graph.vertices.exists(v => v._1 == "a"))
    assert(!graph.vertices("b").upStreams.map(u => u.name).contains("a"))
  }

  it should "addContext" in {
    val graph = Graph(configs)
    graph.build()
    graph.addContext(GraphContext("d", List("c"), List("a", "b"), 1))
    assert(!graph.roots.map(r => r.name).contains("d"))
    assert(graph.roots.map(r => r.name).contains("c"))
    assert(!graph.roots.map(r => r.name).contains("a"))
  }

  it should "addEdge" in {


    val graph = Graph(configs)
    graph.build()

    graph.addEdge("b", "a")
    graph.addEdge("b", "a")
    assert(graph.vertices("a").upStreams.size == 1)
    assert(graph.vertices("b").downStreams.size == 1)
    assert(graph.vertices("a").upStreams.map(u => u.name).contains("b"))
    assert(graph.vertices("b").downStreams.map(d => d.name).contains("a"))
  }

  it should "hasCycle" in {
    val configs = List[GraphContext[Int]](
      GraphContext("a", List(), List(), 1),
      GraphContext("b", List("a"), List(), 1),
      GraphContext("c", List("b"), List("a"), 1)
    )

    val graph = Graph(configs)
    graph.build()
    assert(graph.hasCycle)

    graph.removeEdge("c", "a")
    assert(!graph.hasCycle)
  }

  it should "toPNOrder" in {
    val graph = Graph(configs)
    graph.build()
    assert(graph.toPNOrder.length == 4)
    assert(graph.toPNOrder.map(v => v.name).equals(List("b", "a", "b", "c")))

    graph.addContext(GraphContext("d", List("a"), List(), 1)).addContext(GraphContext("e", List("b"), List(), 1)).addContext(GraphContext("f", List("b"), List(), 1))

    assert(graph.toPNOrder.map(v => v.name).equals(List("e", "f", "b", "d", "a", "e", "f", "b", "c")))
  }

  it should "DFS" in {
    val graph = Graph(configs)
    graph.build()
    assert(graph.DFS().size == 3)
    assert(graph.DFS().map(x => x.name).equals(List("b", "a", "c")))
  }

  it should "addVertex" in {
    val graph = Graph(configs)
    graph.build()
    graph.addVertex(Vertex("d", ListBuffer(), ListBuffer(), 1))
    assert(graph.vertices.size == 4)
    assert(graph.vertices.contains("d"))
  }
}
