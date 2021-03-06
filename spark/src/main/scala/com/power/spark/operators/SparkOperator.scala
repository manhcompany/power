package com.power.spark.operators

import com.power.core.engine.stackmachine.{NormalOperator, Operator}
import com.power.spark.utils._
import org.apache.spark.sql.DataFrame

import scala.util.Try


class SparkOperator extends SparkOperatorFactory {

  override def factory(config: Configuration): Option[Operator[DataFrame]] = {
    Try(Some(config.getOperatorName match {
      case "INPUT" => InputOperator(config.asInstanceOf[SourceConfiguration])
      case "OUTPUT" => OutputOperator(config.asInstanceOf[SinkConfiguration])
      case "SELECT" => SelectExprOperator(config.asInstanceOf[ActionConfiguration])
      case "UNION" => UnionOperator(config.asInstanceOf[ActionConfiguration])
      case "REPARTITION" => RepartitionOperator(config.asInstanceOf[ActionConfiguration])
      case "AS_TEMP_TABLE" => AsTempTableOperator(config.asInstanceOf[ActionConfiguration])
      case "SQL" => SqlOperator(config.asInstanceOf[ActionConfiguration])
      case "RENAME" => RenameOperator(config.asInstanceOf[ActionConfiguration])
      case "DEDUPLICATE" => DeduplicateOperator(config.asInstanceOf[ActionConfiguration])
      case "DROPNULL" => DropNullOperator(config.asInstanceOf[ActionConfiguration])
      case "FILTER" => FilterOperator(config.asInstanceOf[ActionConfiguration])
      case "JOIN" => JoinOperator(config.asInstanceOf[ActionConfiguration])
      case "EXCEPT" => ExceptOperator(config.asInstanceOf[ActionConfiguration])
      case "STORE" => SparkOperatorFactory.pm StoreOperator config.asInstanceOf[MemoryOperatorConfiguration].name
      case "LOAD" => SparkOperatorFactory.pm LoadOperator config.asInstanceOf[MemoryOperatorConfiguration].name
      case "FLATTEN" => FlattenOperator(config.asInstanceOf[ActionConfiguration])
    })).map(d => d).recover { case _: Throwable => None }.get
  }

  case class FlattenOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      Some(config.columns.get.map(x => operands.head.map(d => d.selectExpr(x))).map(_.get).reduce(_ union _))
    }
  }

  case class InputOperator(config: SourceConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = {
      config.load match {
        case Some(_) => 1
        case None => 0
      }
    }
    override val execute: NormalOperatorType = operands => {
      config.load match {
        case Some(_) => operands.head
        case None =>
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
      operands.head.map(_.selectExpr(config.exprs.get: _*))
    }
  }

  case class UnionOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    assert(config.numberOfDatasets.isDefined)
    override val getNumberOfInputs: Int = config.numberOfDatasets.get
    override val execute: NormalOperatorType = operands => {
      operands.tail.foldLeft(operands.head)((d, o) => d.map(x => x.union(o.get)))
    }
  }

  case class RepartitionOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      operands.head.map(_.repartition(config.partitions.get))
    }
  }

  case class AsTempTableOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      val df = operands.head.get
      df.createOrReplaceTempView(config.tableName.get)
      Some(df)
    }
  }

  case class SqlOperator(confg: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 0
    override val execute: NormalOperatorType = _ => {
      Some(SparkCommon.spark.sql(confg.sql.get))
    }
  }

  case class RenameOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      operands.head.map(x => {
        config.columns.get.foldLeft(x)((r, c) => {
          val oldCol = c.split(">>")(0).trim
          val newCol = c.split(">>")(1).trim
          r.withColumnRenamed(oldCol, newCol)
        })
      })
    }
  }

  case class DeduplicateOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      operands.head.map(df => {
        config.columns match {
          case Some(cs) => df.dropDuplicates(cs)
          case None => df.dropDuplicates()
        }
      })
    }
  }

  case class DropNullOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      operands.head.map(df => config.columns match {
        case Some(cs) => df.na.drop(cs.map(x => x.trim))
        case None => df.na.drop()
      })
    }
  }

  case class FilterOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      operands.head.map(df => {
        config.exprs.get.foldLeft(df)((r, c) => r.filter(c))
      })
    }
  }

  case class JoinOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 2
    override val execute: NormalOperatorType = operands => {
      val left = operands.tail.head.get
      val right = operands.head.get
      Some(left.join(right, config.exprs.head, config.joinType.get))
    }
  }

  case class ExceptOperator(config: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 2
    override val execute: NormalOperatorType = operands => {
      operands.tail.head.map(_.except(operands.head.get))
    }
  }
}