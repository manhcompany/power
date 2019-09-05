package com.power.core.graph

import scala.collection.mutable.ListBuffer

case class Vertex[T](name: String, upStreams: ListBuffer[Vertex[T]], downStreams: ListBuffer[Vertex[T]], payLoad: T) {
  def getName: String = name
  def getUpStreams: ListBuffer[Vertex[T]] = upStreams
  def getDownStreams: ListBuffer[Vertex[T]] = downStreams
  def getPayload: T = payLoad

  override def equals(obj: Any): Boolean = {
    if(!obj.isInstanceOf[Vertex[T]]) false
    else name.equals(obj.asInstanceOf[Vertex[T]].getName)
  }

  def addDownStream(value: Vertex[T]): Unit = {
    if(!downStreams.contains(value)) downStreams += value
  }

  def addUpStream(value: Vertex[T]): Unit = {
    if(!upStreams.contains(value)) upStreams += value
  }

  override def toString: String = name
}
