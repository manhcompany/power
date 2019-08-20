package com.power.core.graphxxx

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Vertex[T](name: String, upStreams: mutable.ListBuffer[Vertex[T]], downStreams: mutable.ListBuffer[Vertex[T]], payLoad: T) {
  def getName: String = name
  def getPayload: T = payLoad
  def getUpStreams: ListBuffer[Vertex[T]] = upStreams
  def getDownStreams: ListBuffer[Vertex[T]] = downStreams

  override def equals(obj: Any): Boolean = {
    if(!obj.isInstanceOf[Vertex[T]]) false
    else name.equals(obj.asInstanceOf[Vertex[T]].getName)
  }
}