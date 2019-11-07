package com.power.spark.apps

import com.power.core.engine.stackmachine.{CanonicalStackMachine, Operator, OperatorAdapter}
import com.power.core.util.Logging
import com.power.spark.parser.Parser
import com.power.spark.utils.{Config, SparkOperatorFactory}
import org.apache.spark.sql.DataFrame



object ETL extends Logging {
  def main(args: Array[String]): Unit = {
    execute()
  }

  def execute(): Unit = {
    val config = Config.loadConfig("etl")
    val labels = config.groupBy(x => x._2.label)
    val branches = labels.map(x => {
      val graph = Parser.buildGraph(x._2)
      val operators = graph.toPNOrderOptimize.foldLeft(Seq[Operator[DataFrame]]())((r, c) => SparkOperatorFactory.factory(c.payLoad).get +: r).reverse
      (x._1, operators.map(OperatorAdapter.operator2stack))
    })
    CanonicalStackMachine.execute(branches)
  }
}
