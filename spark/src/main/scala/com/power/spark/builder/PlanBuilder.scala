package com.power.spark.builder

import com.power.spark.utils.SparkConfiguration

trait PlanBuilder {
  type ConfigurationType = Map[String, SparkConfiguration] => Seq[GroupConfiguration]
}
