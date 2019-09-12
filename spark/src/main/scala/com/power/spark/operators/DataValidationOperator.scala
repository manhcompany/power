package com.power.spark.operators

import com.power.core.engine.stackmachine.{NormalOperator, Operator}
import com.power.spark.utils.{ActionConfiguration, Configuration, SinkConfiguration, SourceConfiguration, SparkOperatorFactory}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.catalyst.parser.SqlBaseParser.DescribeColNameContext

import scala.util.Try

class DataValidationOperator extends SparkOperatorFactory {

  /**
    * Describe
    * Run sppark build-in command: dataframe.describe. Then convert result to dataframe
    *
    * +-------+------------------+
    * |summary|         sms_count|
    * +-------+------------------+
    * |  count|           8018085|
    * |   mean|406.19912273317135|
    * | stddev| 16659.37611464105|
    * |    min|              -125|
    * |    max|            777826|
    * +-------+------------------+
    *
    * Convert to
    *
    * +----------+---------+--------+-----------+------+------------------+
    * |time_stamp|date_time| dataset|column_name|   key|             value|
    * +----------+---------+--------+-----------+------+------------------+
    * |1558343251| 20190517|recharge|  sms_count|stddev|16398.940313807667|
    * |1558343251| 20190517|recharge|  sms_count| count|         7379359.0|
    * |1558343251| 20190517|recharge|  sms_count|  mean| 397.3996355238985|
    * |1558343251| 20190517|recharge|  sms_count|   min|            -125.0|
    * |1558343251| 20190517|recharge|  sms_count|   max|          777826.0|
    * +----------+---------+--------+-----------+------+------------------+
    *
    * @param configuration config
    * @return 6-tuple dataframe: "time_stamp", "date_time", "dataset", "column_name", "key", "value"
    */
  case class DescribeOperator(configuration: ActionConfiguration) extends NormalOperator[DataFrame] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {

      val sqlContext = SparkSession.builder().getOrCreate().sqlContext
      import sqlContext.implicits._

      val timestamp: Long = System.currentTimeMillis / 1000
      val columns: Seq[String] = Seq("time_stamp", "date_time", "dataset", "column_name", "key", "value")
      val cols = configuration.describes.map(x => x.col.get)
      val describeResult = operands.head.get.describe(cols: _*)
      describeResult.cache()
      val result = configuration.describes.map(x => {
        val colName = x.col.get
        val summaries = x.summary
        summaries.map(summary => {
          val value = Try(describeResult.select(colName).filter(s"summary = '$summary'").first().get(0).asInstanceOf[String].toDouble)
            .getOrElse(null.asInstanceOf[Double])
          val date = configuration.options.get.filter(x => x.key.equals("date")).map(x => x.value.toString).head
          val dataset = configuration.options.get.filter(x => x.key.equals("dataset")).map(x => x.value.toString).head
          Seq((timestamp, date, dataset, s"desc_$colName", summary,
            value)).toDF(columns: _*)
        }
        ).reduce(_ union _)}).reduce(_ union _)
      describeResult.unpersist()
      Some(result)
    }
  }

  override def factory(config: Configuration): Option[Operator[DataFrame]] = {
    Try(Some(config.getOperatorName match {
      case "DESC" => DescribeOperator(config.asInstanceOf[ActionConfiguration])
    })).map(d => d).recover { case _: Throwable => None }.get
  }
}


