package com.power.core.graph

class GroupConfiguration(val elements: Seq[ElementConfiguration], val downStreams: Seq[GroupConfiguration]) extends Configuration {
  val getElements: Seq[ElementConfiguration] = elements
  val getDownStreams: Seq[GroupConfiguration] = downStreams

  def explore(): List[GroupConfiguration] = {
    def exploreRec(configuration: GroupConfiguration): List[GroupConfiguration] = {
      configuration::configuration.downStreams.foldLeft(List[GroupConfiguration]())((r, e) => exploreRec(e) ::: r)
    }
    exploreRec(this).reverse
  }

  def expand(): ElementConfiguration = {
    def explore(element: ElementConfiguration): List[ElementConfiguration] = {
      element::element.getDownStreams.foldLeft(List[ElementConfiguration]())((r, e) => explore(e):::r)
    }
    explore(elements.head).reverse.head
  }
}
