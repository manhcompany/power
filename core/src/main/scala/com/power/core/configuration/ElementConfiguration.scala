package com.power.core.configuration

class ElementConfiguration(val name: String, val dependencies: Seq[GroupConfiguration], downStreams: Seq[ElementConfiguration]) extends Configuration {
  val getDependencies: Seq[GroupConfiguration] = dependencies

  override def expand(): Configuration = {
    val newDependencies = dependencies.map(d => d.expand())
    val newDownStreams: Seq[ElementConfiguration] = downStreams ++: newDependencies
    new ElementConfiguration(name = name, dependencies = Seq[GroupConfiguration](), downStreams = newDownStreams)
  }

  def getDownStreams: Seq[ElementConfiguration] = downStreams

  /**
    * Build a tree that has elementConfiguration as root
    * @param elementConfiguration root of tree
    * @return
    */
  def buildElementTree(elementConfiguration: ElementConfiguration): ElementConfiguration = {
    elementConfiguration
  }
}