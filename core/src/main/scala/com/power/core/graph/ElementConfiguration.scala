package com.power.core.graph

class ElementConfiguration(val dependencies: Seq[GroupConfiguration], downStreams: Seq[ElementConfiguration]) extends Configuration {
  val getDownStreams: Seq[ElementConfiguration] = downStreams

  def explore(): List[ElementConfiguration] = {
    def exploreRec(elementConfiguration: ElementConfiguration): List[ElementConfiguration] = {
      elementConfiguration::elementConfiguration.getDownStreams.foldLeft(List[ElementConfiguration]())((r, e) => exploreRec(e):::r)
    }
    exploreRec(this).reverse
  }

  def expand(): ElementConfiguration = {
    val newDownStreams = dependencies.map(d => d.expand())++downStreams
    new ElementConfiguration(List(), newDownStreams)
  }
}