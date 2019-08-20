package com.power.core.graph

trait Configuration {
  val getDownStreams: Seq[Configuration]
}