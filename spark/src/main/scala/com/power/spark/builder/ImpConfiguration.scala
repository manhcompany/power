package com.power.spark.builder

abstract class ImpConfiguration {
  val name: String
  def getName: String = name

  override def equals(obj: Any): Boolean = {
    obj match {
      case configuration: ImpConfiguration =>
        configuration.name == name
      case _ => false
    }
  }
}
