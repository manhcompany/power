package com.power.spark.operators

import com.power.core.engine.stackmachine.{NormalOperator, Operator}
import com.power.spark.utils._
import org.apache.spark.sql.DataFrame

import scala.util.Try



class SparkOperator extends SparkOperatorFactory {
  case class InputOperator(config: SourceConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 0
    override val execute: NormalOperatorType = _ => {
      val spark = SparkCommon.getSparkSession()
      val readerFormat = config.format match {
        case Some(f) => spark.read.format(f)
        case None => spark.read
      }

      val readerOptions = config.options match {
        case Some(opt) => opt.foldLeft(readerFormat)((r, o) => r.option(o.key, o.value))
        case None => readerFormat
      }

      Some(readerOptions.load(config.path.get))
    }
  }

  case class OutputOperator(config: SinkConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      val writePartitionBy = config.partitionBy match {
        case Some(fields) => operands.head.get.write.partitionBy(fields: _*)
        case None => operands.head.get.write
      }

      val writeMode = config.mode match {
        case Some(mode) => writePartitionBy.mode(mode)
        case None => writePartitionBy
      }

      val writeFormat = config.format match {
        case Some(format) => writeMode.format(format)
        case None => writeMode
      }

      (config.options match {
        case Some(opts) => opts.foldLeft(writeFormat)((w, opt) => w.option(opt.key, opt.value))
        case None => writeFormat
      }).save(config.path.get)

      None
    }
  }

  case class SelectExprOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      val df = operands.head.get
      Some(df.selectExpr(config.select.get: _*))
    }
  }

  override def factory(config: Configuration): Option[Operator[DataFrame]] = {
    Try(Some(config.getOperatorName match {
      case "INPUT" => InputOperator(config.asInstanceOf[SourceConfiguration])
      case "OUTPUT" => OutputOperator(config.asInstanceOf[SinkConfiguration])
      case "SELECT" => SelectExprOperator(config.asInstanceOf[ActionConfiguration])
    })).map(d => d).recover { case _: Throwable => None }.get
  }
}




