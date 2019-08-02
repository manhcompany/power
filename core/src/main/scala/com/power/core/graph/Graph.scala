package com.power.core.graph


object Graph {

  def makeGraph[T](configurations: Seq[Configuration]): Seq[Vertex[T]] = {
    List[Vertex[T]]()
  }

  /**
    * Check tree has cycle
    * @param vertex root of tree
    * @return true if tree has cycle, other false
    */
  def hasCycle[T](vertex: Vertex[T]): Boolean = {
    def checkCycleRec(vertex: Vertex[T], checked: List[Vertex[T]]): List[Vertex[T]] = {
      if(checked.contains(vertex))
        List[Vertex[T]](vertex)
      else {
        val newChecked = vertex::checked
        vertex.getDownStreams.flatMap(v => checkCycleRec(v, newChecked)).toList
      }
    }
    checkCycleRec(vertex, List[Vertex[T]]()).nonEmpty
  }

  /**
    * Explore a tree by children -> parent recursive order. Result as Polish Notation
    * @param vertex Vertex as root of tree
    * @return List of vertex that explore
    */
  def explore[T](vertex: Vertex[T]): List[Vertex[T]] = {

    def exploreRec(vertex: Vertex[T]): List[Vertex[T]] = {
      vertex::vertex.getDownStreams.foldLeft(List[Vertex[T]]())((r, v) => exploreRec(v):::r)
    }

    exploreRec(vertex).reverse
  }
}
