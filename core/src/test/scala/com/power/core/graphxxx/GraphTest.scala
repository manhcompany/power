package com.power.core.graphxxx

import com.power.core.configuration.{ElementConfiguration, GroupConfiguration}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

import scala.collection.mutable.ListBuffer

class GraphTest extends FlatSpec {

  val eB1 = new ElementConfiguration(name = "eB1", dependencies = List(), downStreams = List())
  val eB2 = new ElementConfiguration(name = "eB2", dependencies = List(), downStreams = List(eB1))

  val eC1 = new ElementConfiguration(name = "eC1", dependencies = List(), downStreams = List())
  val eC2 = new ElementConfiguration(name = "eC2", dependencies = List(), downStreams = List(eC1))

  val gB = new GroupConfiguration(name = "gB", element = eB2, downStreams = List())
  val gC = new GroupConfiguration(name = "gC", element = eC2, downStreams = List())

  val eA1 = new ElementConfiguration(name = "eA1", dependencies = List(), downStreams = List())
  val eA2 = new ElementConfiguration(name = "eA2", dependencies = List(gB), downStreams = List(eA1))
  val eA3 = new ElementConfiguration(name = "eA3", dependencies = List(gC), downStreams = List(eA2))

  val gA = new GroupConfiguration(name = "gA", element = eA3, downStreams = List())


  behavior of "GraphTest"

  it should "explore" in {
    val v9 = new Vertex(name = "v9", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    val v10 = new Vertex(name = "v10", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v9), payLoad = 0)
    val v11 = new Vertex(name = "v11", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v10), payLoad = 0)
    val v5 = new Vertex(name = "v5", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    val v6 = new Vertex(name = "v6", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v5), payLoad = 0)
    val v7 = new Vertex(name = "v7", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v6), payLoad = 0)
    val v8 = new Vertex(name = "v8", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v7), payLoad = 0)
    val v12 = new Vertex(name = "v12", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v8, v11), payLoad = 0)
    val v13 = new Vertex(name = "v13", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v12), payLoad = 0)
    val v4 = new Vertex(name = "v4", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    val v3 = new Vertex(name = "v3", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v4, v8), payLoad = 0)
    val v2 = new Vertex(name = "v2", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v3, v13), payLoad = 0)
    val v1 = new Vertex(name = "v1", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v2), payLoad = 0)
    val result = new Graph().explore(v1)
    val expectation = List[Vertex[Int]](v4, v5, v6, v7, v8, v3, v5, v6, v7, v8, v9, v10, v11, v12, v13, v2, v1)
    assert(result == expectation)
  }

  it should "has cycle is true" in {
    val v4 = new Vertex(name = "v1", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    val v3 = new Vertex(name = "v3", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v4), payLoad = 0)
    val v2 = new Vertex(name = "v2", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v3), payLoad = 0)
    val v1 = new Vertex(name = "v1", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v2), payLoad = 0)
    assert(new Graph().hasCycle(v1))
  }

  it should "has cycle is false" in {
    val v3 = new Vertex(name = "v3", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    val v2 = new Vertex(name = "v2", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v3), payLoad = 0)
    val v1 = new Vertex(name = "v1", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](v2), payLoad = 0)
    assert(!new Graph().hasCycle(v1))
  }

  it should "makeGraph" in {
    val result = new Graph().makeGraph[Int](ListBuffer(gA, gB, gC))
    assert(result.length == 0)
  }

}
