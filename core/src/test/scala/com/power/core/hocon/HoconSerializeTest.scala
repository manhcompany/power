package com.power.core.hocon

import com.power.core.util.HoconSerialize
import com.typesafe.config._
import org.scalatest.FlatSpec

case class Pet(name: String, age: Int) extends HoconSerialize

case class Person(name: String, age: Int, pets: List[Pet], properties: Map[String, Property], works: Map[String, String], list: List[String]) extends HoconSerialize

case class Property(value: Double) extends HoconSerialize

class HoconSerializeTest extends FlatSpec {
  it should "reflection" in {

    def dump(config: Config): Unit = {
      val renderOpts = ConfigRenderOptions.defaults.setOriginComments(false).setComments(false).setJson(false)
      println(config.root().render(renderOpts))
    }

    val person = Person("A", 19,
      List(Pet("mimi", 2), Pet("milu", 1)),
      Map("house" -> Property(1000000.0), "car" -> Property(50000.0)),
      Map("a" -> "b", "b" -> "c"),
      List("a", "b", "c")
    )

    val config = ConfigFactory.empty()
      .withValue("root", person.toHocon)

    dump(config)
  }
}
