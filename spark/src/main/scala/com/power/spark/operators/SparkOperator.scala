package com.power.spark.operators

import com.power.core.engine.stackmachine.{NormalOperator, Operator}
import com.power.spark.utils.{Configuration, SparkOperatorFactory}
import org.apache.spark.sql.DataFrame

import scala.util.Try

class SparkOperator extends SparkOperatorFactory {
  case class InputOperator(config: Configuration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 0
    override val execute: NormalOperatorType = _
  }

  case class OutputOperator(config: Configuration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = _
  }

  case class SelectExprOperator(config: Configuration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = _
  }

  override def factory(config: Configuration): Option[Operator[DataFrame]] = {
    Try(Some(config.getOperatorName match {
      case "INPUT" => InputOperator(config)
      case "OUTPUT" => OutputOperator(config)
      case "SELECT" => SelectExprOperator(config)
    })).map(d => d).recover { case _: Throwable => None }.get
  }
}




