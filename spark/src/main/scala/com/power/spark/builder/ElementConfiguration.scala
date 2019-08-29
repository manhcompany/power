package com.power.spark.builder

class ElementConfiguration[T](val name: String, val payload: T, val dependencies: Seq[GroupConfiguration[T]], downStreams: Seq[ElementConfiguration[T]]) extends Configuration {
  val getDependencies: Seq[GroupConfiguration[T]] = dependencies

  override def expand(): Configuration = {
    val newDependencies = dependencies.map(d => d.expand())
    val newDownStreams: Seq[ElementConfiguration[T]] = downStreams ++: newDependencies
    new ElementConfiguration(name = name, payload = payload, dependencies = Seq[GroupConfiguration[T]](), downStreams = newDownStreams)
  }

  def getDownStreams: Seq[ElementConfiguration[T]] = downStreams

  /**
    * Build a tree that has elementConfiguration as root
    * @param elementConfiguration root of tree
    * @return
    */
  def buildElementTree(elementConfiguration: ElementConfiguration[T]): ElementConfiguration[T] = {
    elementConfiguration
  }
}