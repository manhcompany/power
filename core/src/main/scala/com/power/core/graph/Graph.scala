package com.power.core.graph


object Graph {

  def makeGraph(configurations: Seq[Configuration]): Seq[Vertex] = {
    List[Vertex]()
  }

  def checkCycle(vertex: Vertex): Boolean = {
    false
  }

  def explore(vertex: Vertex): List[Vertex] = {

    def exploreRec(vertex: Vertex): List[Vertex] = {
      vertex::vertex.getDownStreams.foldLeft(List[Vertex]())((r, v) => exploreRec(v):::r)
    }

    exploreRec(vertex).reverse
  }
}
