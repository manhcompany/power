package com.power.spark.hocontest

import com.typesafe.config.{Config, ConfigFactory, ConfigObject, ConfigRenderOptions, ConfigValueFactory}
import org.scalatest.FlatSpec

import scala.collection.JavaConversions._

class HoconDump extends FlatSpec {

  it should "Dump hocon" in {
    val results = Map("/data/vodafone/clean/recharge/trx_date=20191001" -> Map("channel" -> Some("facet"), "talktime_faceval" -> None, "circle_id" -> Some("facet"), "original_circle_id" -> Some("facet"), "sid_group" -> Some("facet"), "trx_time" -> None, "promo_id" -> Some("describe"), "mrp_amount" -> Some("describe"), "sid" -> Some("describe")))

    results.foreach(x => {
      println(x._1)
      val config = buildUniqSid(x._2, buildFacet(x._2, builDescribe(x._2, buildRaw(x._2, initConfig()))))
      dump(config)
    })

    def buildUniqSid(map: Map[String, Option[String]], config: Config): Config = {
      val sidFields = map.filter(x => x._1 == "sid")
      if (sidFields.nonEmpty) {
        val a = List(
          ConfigValueFactory.fromMap(Map("plan" -> "SELECT", "exprs" -> ConfigValueFactory.fromIterable(List("sid")))),
          ConfigValueFactory.fromMap(Map("plan" -> "AGG", "cols" -> ConfigValueFactory.fromIterable(List("sid")), "options" -> ConfigValueFactory.fromMap(Map("sid" -> "count")))),
          ConfigValueFactory.fromMap(Map("plan" -> "SELECT", "exprs" -> ConfigValueFactory.fromIterable(List("sid as unique_sid")))),
          ConfigValueFactory.fromMap(Map("plan" -> "DESCRIBE", "options" -> ConfigValueFactory.fromMap(Map("date" -> "${date}", "dataset" -> "${dataset}")), "describes" -> ConfigValueFactory.fromIterable(List(
            ConfigValueFactory.fromMap(Map("col" -> "unique_sid", "summary" -> ConfigValueFactory.fromIterable(List("count"))))
          ))))
        )
        config.withValue("com.trustingsocial.spark.uniqueuser.input", ConfigValueFactory.fromMap(Map("format" -> "conf.raw")))
          .withValue("com.trustingsocial.spark.uniqueuser.actions", ConfigValueFactory.fromIterable(a))
      } else config
    }

    def buildFacet(map: Map[String, Option[String]], config: Config): Config = {
      val facetFields = map.filter(x => {
        x._2 match {
          case Some("facet") => true
          case _ => false
        }
      })

      if (facetFields.nonEmpty) {
        val cols = facetFields.keys
        config.withValue("com.trustingsocial.spark.facet.input", ConfigValueFactory.fromMap(Map("format" -> "conf.raw")))
          .withValue("com.trustingsocial.spark.facet.actions", ConfigValueFactory.fromIterable(List(
            ConfigValueFactory.fromMap(Map("plan" -> "FACET", "cols" -> ConfigValueFactory.fromIterable(cols.toList), "options" -> ConfigValueFactory.fromMap(Map("date" -> "${date}", "dataset" -> "${dataset}"))))
          )))
      } else config
    }

    def builDescribe(map: Map[String, Option[String]], config: Config): Config = {
      val describeFields = map.filter(x => {
        x._2 match {
          case Some("describe") => true
          case _ => false
        }
      })
      if (describeFields.nonEmpty) {

        val describes = describeFields.foldLeft(List[ConfigObject]())((c, x) => {
          ConfigValueFactory.fromMap(Map("col" -> x._1, "summary" -> ConfigValueFactory.fromIterable(List("count", "mean", "stddev", "min", "max")))) :: c
        })
        config.withValue("com.trustingsocial.spark.describe.input", ConfigValueFactory.fromMap(Map("format" -> "conf.raw")))
          .withValue("com.trustingsocial.spark.describe.actions", ConfigValueFactory.fromIterable(List(
            ConfigValueFactory.fromMap(Map("plan" -> "DESCRIBE", "options" -> ConfigValueFactory.fromMap(Map("date" -> "${date}", "dataset" -> "${dataset}")), "describes" -> ConfigValueFactory.fromIterable(describes)))
          )))
      }
      else config
    }

    def buildRaw(map: Map[String, Option[String]], config: Config): Config = {
      val nullExprs = map.foldLeft(List[String]())((r, x) => {
        val fieldName = s"${x._1}_null"
        s"""case when ${x._1} is null or length(trim(${x._1})) = 0 then "null" else "not null" end $fieldName""" :: r
      })
      val exprs = map.filter(x => {
        x._2 match {
          case Some(_) => true
          case None => false
        }
      }).foldLeft(nullExprs)((r, x) => {
        x._1 :: r
      })
      config
        .withValue("com.trustingsocial.spark.raw.input", ConfigValueFactory.fromMap(Map("input" -> "parquet", "options" -> ConfigValueFactory.fromMap(Map("STREAMING" -> false)))))
        .withValue("com.trustingsocial.spark.raw.input.actions", ConfigValueFactory.fromIterable(List(
          ConfigValueFactory.fromMap(Map("plan" -> "SELECT", "exprs" -> ConfigValueFactory.fromIterable(exprs)))
        )))
    }

    def initConfig(): Config = {
      val config = ConfigFactory.empty()
        .withValue("com.trustingsocial.spark.result.input.format", ConfigValueFactory.fromAnyRef("conf.facet"))
        .withValue("com.trustingsocial.spark.result.actions", ConfigValueFactory.fromIterable(List[ConfigObject](
          ConfigValueFactory.fromMap(Map("plan" -> "UNION", "options" -> ConfigValueFactory.fromMap(Map("OTHER_DATASETS" -> "describe,uniqueuser")))),
          ConfigValueFactory.fromMap(Map("plan" -> "SELECT", "exprs" -> ConfigValueFactory.fromIterable(List(
            "cast(time_stamp as long) as time_stamp",
            """cast(substr(date_time, 1, 4) as int) as year""",
            """cast(substr(date_time, 5, 2) as int) as month""",
            """cast(substr(date_time, 7, 2) as int) as day""",
            """dataset""",
            """column_name""",
            """key""",
            """cast(value as double) as value"""
          )))),
          ConfigValueFactory.fromMap(Map("plan" -> "REPARTITION", "options" -> ConfigValueFactory.fromMap(Map("PARTITIONS" -> 1))))
        )))
        .withValue("com.trustingsocial.spark.result.output", ConfigValueFactory.fromMap(Map("format" -> "parquet", "options" -> ConfigValueFactory.fromMap(Map("OUTPUT_MODE" -> "append")))))

      config
    }

    def dump(config: Config): Unit = {
      val renderOpts = ConfigRenderOptions.defaults.setOriginComments(false).setComments(false).setJson(false)
      println(config.root().render(renderOpts))
    }
  }
}
