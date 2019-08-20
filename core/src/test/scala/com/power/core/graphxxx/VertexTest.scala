package com.power.core.graphxxx

import org.scalatest.FlatSpec

import scala.collection.mutable.ListBuffer

class VertexTest extends FlatSpec {

  behavior of "VertexTest"

  it should "equals" in {
    val vertex = new Vertex(name = "v", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    val vertex1 = new Vertex(name = "v", upStreams = ListBuffer[Vertex[Int]](), ListBuffer[Vertex[Int]](), payLoad = 0)
    assert(vertex == vertex1)
  }

  it should "not equals" in {
    val vertex = new Vertex(name = "v", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    val vertex1 = new Vertex(name = "v1", upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    assert(vertex != vertex1)
  }

  it should "not equals 1" in {
    val vertex = new Vertex(name = "v",  upStreams = ListBuffer[Vertex[Int]](), downStreams = ListBuffer[Vertex[Int]](), payLoad = 0)
    val a = "v"
    assert(vertex != a)
  }

}
