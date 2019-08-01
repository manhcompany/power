package com.power.core.graph

class Vertex(name: String, downStreams: Seq[Vertex]) {
  def getDownStreams: Seq[Vertex] = downStreams
  def getName: String = name

  override def equals(obj: Any): Boolean = {
    if(!obj.isInstanceOf[Vertex]) false
    else name.equals(obj.asInstanceOf[Vertex].getName)
  }
}