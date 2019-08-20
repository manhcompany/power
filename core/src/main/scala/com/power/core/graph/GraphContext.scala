package com.power.core.graph

case class GraphContext[T](name: String, upStreams: Seq[String], downStreams: Seq[String], payLoad: T)