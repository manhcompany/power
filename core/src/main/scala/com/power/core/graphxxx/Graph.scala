package com.power.core.graphxxx

import com.power.core.configuration.{Configuration, ElementConfiguration, GroupConfiguration}

import scala.collection.mutable.ListBuffer


class Graph {

  def makeGraph[T](groupConfigurations: Seq[GroupConfiguration]): Seq[Vertex[T]] = {
    // expand elements
    val mapGroups = groupConfigurations.foldLeft(Map[String, Configuration]())((m, c) => {
      val elements = c.getAllElements
      elements.foldLeft(m)((r, e) => r + (e.getName -> e)) + (c.getName -> c.expand())
    })

    val expandedMap = mapGroups.foldLeft(mapGroups)((m, e) => {
      val mergedElement = {
        val newDownStreams = e._2.asInstanceOf[ElementConfiguration].getDependencies.foldLeft(Seq[ElementConfiguration]())((r, g) => mapGroups(g.getName).asInstanceOf[ElementConfiguration] +: r) ++ e._2.getDownStreams
        new ElementConfiguration(name = e._1, dependencies = e._2.asInstanceOf[ElementConfiguration].getDependencies, downStreams = newDownStreams.map(_.asInstanceOf[ElementConfiguration]))
      }
      m.filter(_._2.asInstanceOf[ElementConfiguration].equals(e._2)).foldLeft(m)((r, fe) => r - fe._1 + (fe._1 -> mergedElement))
    })
    println(expandedMap)
    List[Vertex[T]]()
  }

  /**
    * Explore a tree by children -> parent recursive order. Result as Polish Notation
    *
    * @param vertex Vertex as root of tree
    * @return List of vertex that explore
    */
  def explore[T](vertex: Vertex[T]): List[Vertex[T]] = {
    assert(!hasCycle(vertex), "The tree should not have cycle")

    def exploreRec(vertex: Vertex[T]): List[Vertex[T]] = {
      vertex :: vertex.getDownStreams.foldLeft(List[Vertex[T]]())((r, v) => exploreRec(v) ::: r)
    }

    exploreRec(vertex).reverse
  }

  /**
    * Check tree has cycle
    *
    * @param vertex root of tree
    * @return true if tree has cycle, other false
    */
  def hasCycle[T](vertex: Vertex[T]): Boolean = {
    def checkCycleRec(vertex: Vertex[T], checked: List[Vertex[T]]): List[Vertex[T]] = {
      if (checked.contains(vertex))
        List[Vertex[T]](vertex)
      else {
        val newChecked = vertex :: checked
        vertex.getDownStreams.flatMap(v => checkCycleRec(v, newChecked)).toList
      }
    }

    checkCycleRec(vertex, List[Vertex[T]]()).nonEmpty
  }

  def addEdgeToRoot[T](root: Vertex[T], edge: Edge[T]): Vertex[T] = {
    val start = findVertex(root, edge.getStart)
    val end = findVertex(root, edge.getEnd)

    start match {
      case Some(x) => end match {
        case Some(y) => {
          if(!x.getDownStreams.contains(y)) x.getDownStreams += y
          if(!y.getUpStreams.contains(x)) y.getUpStreams += x
        }
        case None => {
          val y = new Vertex[T](name = edge.getEnd.getName, upStreams = edge.getEnd.getUpStreams += x, downStreams = edge.getEnd.getDownStreams, payLoad = edge.getEnd.getPayload)
          x.getDownStreams += y
        }
      }
      case None => end match {
        case Some(y) => {
          val x = new Vertex[T](name = edge.getStart.getName, upStreams = edge.getStart.getUpStreams, downStreams = edge.getStart.getDownStreams += y, payLoad = edge.getStart.getPayload)
          y.getUpStreams += x
        }
        case None => {
          edge.getStart.getDownStreams += edge.getEnd
          edge.getEnd.getUpStreams += edge.getStart
        }
      }
    }
    root
  }

  def addEdge[T](roots: Seq[Vertex[T]], edge: Edge[T]): Seq[Vertex[T]] = {
    roots.map(root => addEdgeToRoot[T](root, edge))
  }

  def findVertex[T](root: Vertex[T], vertex: Vertex[T]): Option[Vertex[T]] = {
    explore(root).find(p => p == vertex)
  }
}
