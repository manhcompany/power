package com.power.core.graph


object Graph {

  def makeGraph(configurations: Seq[Configuration]): Seq[Vertex] = {
    List[Vertex]()
  }

  /**
    * Check tree has cycle
    * @param vertex root of tree
    * @return true if tree has cycle, other false
    */
  def hasCycle(vertex: Vertex): Boolean = {
    def checkCycleRec(vertex: Vertex, checked: List[Vertex]): List[Vertex] = {
      if(checked.contains(vertex))
        List[Vertex](vertex)
      else {
        val newChecked = vertex::checked
        vertex.getDownStreams.flatMap(v => checkCycleRec(v, newChecked)).toList
      }
    }
    checkCycleRec(vertex, List[Vertex]()).nonEmpty
  }

  /**
    * Explore a tree by children -> parent recursive order. Result as Polish Notation
    * @param vertex Vertex as root of tree
    * @return List of vertex that explore
    */
  def explore(vertex: Vertex): List[Vertex] = {

    def exploreRec(vertex: Vertex): List[Vertex] = {
      vertex::vertex.getDownStreams.foldLeft(List[Vertex]())((r, v) => exploreRec(v):::r)
    }

    exploreRec(vertex).reverse
  }
}
