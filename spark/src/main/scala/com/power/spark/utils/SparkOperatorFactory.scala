package com.power.spark.utils

import java.util.ServiceLoader

import com.power.core.engine.stackmachine.{Operator, ProgramMemory}
import org.apache.spark.sql.DataFrame

import scala.collection.JavaConverters._

trait SparkOperatorFactory {
  def factory(config: Configuration): Option[Operator[DataFrame]]
}

object SparkOperatorFactory {
  val pm = new ProgramMemory[DataFrame]()
  private val factories = ServiceLoader.load(classOf[SparkOperatorFactory]).asScala.toList.map(_.getClass).map(_.newInstance())

  def factory(configuration: Configuration): Option[Operator[DataFrame]] = {
    val operators = factories.foldLeft(List[Operator[DataFrame]]())((result, operatorFactory) => operatorFactory.factory(configuration) match {
      case Some(d) => d :: result
      case None => result
    })

    if(operators.lengthCompare(1) == 0) {
      Some(operators.head)
    }
    else {
      throw new Exception(s"We have ${operators.size} object(s) for ${configuration.getOperatorName} config operator name")
    }
  }
}
