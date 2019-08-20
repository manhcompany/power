package com.power.core.graph

class Vertex[T](name: String, downStreams: Seq[Vertex[T]], payLoad: T) {
  def getDownStreams: Seq[Vertex[T]] = downStreams
  def getName: String = name
  def getPayload: T = payLoad

  override def equals(obj: Any): Boolean = {
    if(!obj.isInstanceOf[Vertex[T]]) false
    else name.equals(obj.asInstanceOf[Vertex[T]].getName)
  }
}