package com.power.core.configuration

class GroupConfiguration(val name: String, val element: ElementConfiguration, val downStreams: Seq[GroupConfiguration]) extends Configuration {
  override def expand(): ElementConfiguration = element

  override val getDownStreams: Seq[Configuration] = downStreams

  def buildDownStreams(): Configuration = {
    def getElements(elementConfiguration: ElementConfiguration): Seq[ElementConfiguration] = {
      def getElementsRec(elementConfiguration: ElementConfiguration): Seq[ElementConfiguration] = {
        elementConfiguration +: elementConfiguration.getDownStreams.foldLeft(Seq[ElementConfiguration]())((r, e) => r ++ getElementsRec(e))
      }

      getElementsRec(elementConfiguration)
    }

    val elements = getElements(element)
    val newDownStreams = elements.foldLeft(downStreams)((r, e) => r ++ e.dependencies)
    new GroupConfiguration(name, element, newDownStreams)
  }

  def getAllElements: Seq[ElementConfiguration] = {
    def rec(elementConfiguration: ElementConfiguration): Seq[ElementConfiguration] = {
      elementConfiguration+:elementConfiguration.getDownStreams.foldLeft(Seq[ElementConfiguration]())((r, e) => rec(e)++r)
    }
    rec(element)
  }
}
