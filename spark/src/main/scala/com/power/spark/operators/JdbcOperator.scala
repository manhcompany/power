package com.power.spark.operators

import java.util.Properties

import com.power.core.engine.stackmachine.{NormalOperator, Operator}
import com.power.spark.utils.{ActionConfiguration, Configuration, MemoryOperatorConfiguration, SinkConfiguration, SourceConfiguration, SparkOperatorFactory}
import org.apache.spark.sql.{DataFrame, SaveMode}

import scala.util.Try

class JdbcOperator extends SparkOperatorFactory {
  override def factory(config: Configuration): Option[Operator[DataFrame]] = {
    Try(Some(config.getOperatorName match {
      case "JDBC-OUTPUT" => JdbcWriteOperator(config.asInstanceOf[SinkConfiguration])
    })).map(d => d).recover { case _: Throwable => None }.get
  }

  case class JdbcWriteOperator(config: SinkConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      val props = new Properties()
      props.setProperty("user", config.username.get)
      props.setProperty("password", config.password.get)

      operands.head.get.write
        .mode(config.mode.getOrElse("ErrorIfExists"))
        .jdbc(config.url.get, config.table.get, props)
      None
    }
  }
}
