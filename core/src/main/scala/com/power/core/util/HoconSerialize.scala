package com.power.core.util

import com.typesafe.config.{ConfigValue, ConfigValueFactory}

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._
import scala.util.Try

class HoconSerialize {

  def toHocon: ConfigValue = {
    val rm = scala.reflect.runtime.currentMirror
    val accessors = rm.classSymbol(this.getClass).toType.members.collect {
      case m: MethodSymbol if m.isGetter && m.isPublic => m
    }
    val instanceMirror = rm.reflect(this)

    val configValue = accessors.foldLeft(Map[String, ConfigValue]())((m, x) => {
      val serialize = instanceMirror.reflectMethod(x).apply()
      serialize match {
        case value: Map[String, _] =>
          value match {
            case s: Map[String, HoconSerialize] =>
              Try(
                m + (x.name.toString -> ConfigValueFactory.fromMap(s.map(y => (y._1, y._2.toHocon))))
              ).getOrElse({
                val map = value.map(y => (y._1, ConfigValueFactory.fromAnyRef(y._2)))
                val e = x.name.toString -> ConfigValueFactory.fromMap(map)
                m + e
              })
            case _ =>
              val map = value.map(y => (y._1, ConfigValueFactory.fromAnyRef(y._2)))
              val e = x.name.toString -> ConfigValueFactory.fromMap(map)
              m + e
          }
        case _ => serialize match {
          case list: List[_] =>
            list match {
              case s: List[HoconSerialize] =>
                Try(
                  m + (x.name.toString -> ConfigValueFactory.fromIterable(s.map(y => y.toHocon)))
                ).getOrElse(
                  m + (x.name.toString -> ConfigValueFactory.fromIterable(list))
                )
              case _ =>
                m + (x.name.toString -> ConfigValueFactory.fromIterable(list))
            }
          case _ =>
            serialize match {
              case s: HoconSerialize =>
                m + (x.name.toString -> s.toHocon)
              case _ =>
                m + (x.name.toString -> ConfigValueFactory.fromAnyRef(serialize))
            }
        }
      }
    })
    ConfigValueFactory.fromMap(configValue)
  }
}