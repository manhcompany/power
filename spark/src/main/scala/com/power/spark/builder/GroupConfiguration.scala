package com.power.spark.builder

class GroupConfiguration[T](val name: String, val payload: T, val element: ElementConfiguration[T], val downStreams: Seq[GroupConfiguration[T]]) extends Configuration {
  override def expand(): ElementConfiguration[T] = element

  override val getDownStreams: Seq[Configuration] = downStreams

  def buildDownStreams(): Configuration = {
    def getElements(elementConfiguration: ElementConfiguration[T]): Seq[ElementConfiguration[T]] = {
      def getElementsRec(elementConfiguration: ElementConfiguration[T]): Seq[ElementConfiguration[T]] = {
        elementConfiguration +: elementConfiguration.getDownStreams.foldLeft(Seq[ElementConfiguration[T]]())((r, e) => r ++ getElementsRec(e))
      }

      getElementsRec(elementConfiguration)
    }

    val elements = getElements(element)
    val newDownStreams = elements.foldLeft(downStreams)((r, e) => r ++ e.dependencies)
    new GroupConfiguration[T](name, payload, element, newDownStreams)
  }

  def getAllElements: Seq[ElementConfiguration[T]] = {
    def rec(elementConfiguration: ElementConfiguration[T]): Seq[ElementConfiguration[T]] = {
      elementConfiguration+:elementConfiguration.getDownStreams.foldLeft(Seq[ElementConfiguration[T]]())((r, e) => rec(e)++r)
    }
    rec(element)
  }
}
