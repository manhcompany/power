package com.power.core.graph.mutable

import com.power.core.graph.{GraphContext, Vertex}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Graph[T](configs: Seq[GraphContext[T]]) extends Iterable[Vertex[T]] {
  val vertices: mutable.Map[String, Vertex[T]] = mutable.Map[String, Vertex[T]]()

  def getConfigs: Seq[GraphContext[T]] = configs

  def getVertices: mutable.Map[String, Vertex[T]] = vertices

  def build(): Unit = {
    createVertices()
    createEdges()
  }

  def createVertices(): Unit = {
    configs.foreach(config => {
      vertices += (config.name -> Vertex[T](config.name, ListBuffer[Vertex[T]](), ListBuffer[Vertex[T]](), config.payLoad))
    })
  }

  def createEdges(): Unit = {
    configs.foreach(config => {
      config.downStreams.foreach(downStream => {
        vertices(config.name).addDownStream(vertices(downStream))
        vertices(downStream).addUpStream(vertices(config.name))
      })
      config.upStreams.foreach(upStream => {
        vertices(config.name).addUpStream(vertices(upStream))
        vertices(upStream).addDownStream(vertices(config.name))
      })
    })
  }

  def removeVertex(name: String): Unit = {
    vertices.find(v => v._1 == name) match {
      case Some(vertex) =>
        vertex._2.upStreams.foreach(u => removeEdge(u.name, vertex._1))
        vertex._2.downStreams.foreach(d => removeEdge(vertex._1, d.name))
        vertices.remove(vertex._1)
      case None =>
    }
  }

  def removeEdge(start: String, end: String): Graph[T] = {
    vertices(start).downStreams.find(d => d.name == end) match {
      case Some(x) => vertices(start).downStreams -= x
      case None =>
    }

    vertices(end).upStreams.find(u => u.name == start) match {
      case Some(x) => vertices(end).upStreams -= x
      case None =>
    }
    this
  }

  // TODO need more test cases
  def addContext(context: GraphContext[T]): Graph[T] = {
    vertices += (context.name -> Vertex[T](context.name, ListBuffer[Vertex[T]](), ListBuffer[Vertex[T]](), context.payLoad))
    context.downStreams.foreach(d => {
      vertices(context.name).addDownStream(vertices(d))
      vertices(d).addUpStream(vertices(context.name))
    })
    context.upStreams.foreach(u => {
      vertices(u).addDownStream(vertices(context.name))
      vertices(context.name).addUpStream(vertices(u))
    })
    this
  }

  def addEdge(start: String, end: String): Graph[T] = {
    vertices(start).addDownStream(vertices(end))
    vertices(end).addUpStream(vertices(start))
    this
  }

  def toPNOrder: Seq[Vertex[T]] = {
    assert(!hasCycle, "Graph should not have any cycle")

    def toPNOrderRec(vertex: Vertex[T]): List[Vertex[T]] = {
      vertex :: vertex.downStreams.toList.foldLeft(List[Vertex[T]]())((r, v) => toPNOrderRec(v) ::: r)
    }

    roots.map(r => toPNOrderRec(r).reverse).reduce((x, y) => x ::: y)
  }

  def hasCycle: Boolean = {
    def hasCycleRec(vertex: Vertex[T], checked: List[Vertex[T]]): List[Vertex[T]] = {
      if (checked.contains(vertex)) List[Vertex[T]](vertex)
      else {
        val newChecked = vertex :: checked
        vertex.downStreams.flatMap(v => hasCycleRec(v, newChecked)).toList
      }
    }

    roots.isEmpty || roots.map(r => hasCycleRec(r, List[Vertex[T]]()).nonEmpty).reduce((x, y) => x || y)
  }

  def roots: Seq[Vertex[T]] = {
    vertices.values.filter(v => v.upStreams.isEmpty).toList
  }

  def DFS(): List[Vertex[T]] = {
    def DFSRec(vertex: Vertex[T], visited: List[Vertex[T]]): List[Vertex[T]] = {
      if(visited.contains(vertex))
        visited
      else {
        vertex.downStreams.filterNot(p => visited.contains(p)).foldLeft(vertex :: visited)((r, x) => DFSRec(x, r))
      }
    }
    roots.foldLeft(List[Vertex[T]]())((r, root) => DFSRec(root, r).reverse)
  }

  def moveNodeToRoot(node: String): Graph[T] = {
    val addedContext = vertices(node).upStreams.foldLeft(List[(String, String)]())((r, u) => r ::: {
      vertices(node).downStreams.foldLeft(List[(String, String)]())((rr, d) => {
        (u.name, d.name)
      } :: rr)
    })
    val removedEdges = vertices(node).upStreams.foldLeft(List[(String, String)]())((r, u) => (u.name, vertices(node).name) :: r)
    addedContext.foreach(x => this.addEdge(x._1, x._2))
    removedEdges.foreach(x => this.removeEdge(x._1, x._2))
    this
  }

  override def iterator: Iterator[Vertex[T]] = {
    DFS().toIterator
  }
}
