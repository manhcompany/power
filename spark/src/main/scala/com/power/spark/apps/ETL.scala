package com.power.spark.apps

import com.power.core.engine.stackmachine.{CanonicalStackMachine, NormalOperator, Operator, OperatorAdapter, StackOperator}
import com.power.core.util.Logging
import com.power.spark.parser.Parser
import com.power.spark.utils.{Config, SparkOperatorFactory}
import org.apache.spark.sql.DataFrame



object ETL extends Logging {
  def main(args: Array[String]): Unit = {
    val config = Config.loadConfig("etl")
    val graph = Parser.buildGraph(config)
    val operators = graph.toPNOrder.foldLeft(Seq[Operator[DataFrame]]())((r, c) => SparkOperatorFactory.factory(c.payLoad).get +: r).reverse
    val branches = Map[String, Seq[StackOperator[DataFrame]]](("main", operators.map(OperatorAdapter.operator2stack)))
    CanonicalStackMachine.execute(branches)
  }
}
