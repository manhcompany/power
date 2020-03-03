package com.power.core.hocon

import com.power.core.util.HoconSerialize
import com.typesafe.config._
import org.scalatest.FlatSpec
import com.typesafe.config.ConfigFactory

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

  it should "load config" in {
    val config = ConfigFactory.load("conf/hocon-serialize.conf")
    val person = pureconfig.loadConfigOrThrow[Person](conf = config, "root")
    println(person)
    assert(person.isInstanceOf[Person])
    assert(person.age == 19)
  }

  it should "regex test" in {
    val substitutionPattern = """[$][{]?[a-zA-Z]+[a-zA-Z0-9.]*[}]?""".r
    println(substitutionPattern)
    val substitutions = substitutionPattern.findAllIn(
      """
        |v.age = 19
        |root {
        |    age=${v.age}
        |    list=[
        |        a,
        |        b,
        |        c
        |    ]
        |    name=${v.age}
        |    pets=[
        |        {
        |            age=2
        |            name=mimi
        |        },
        |        {
        |            age=1
        |            name=milu
        |        }
        |    ]
        |    properties {
        |        car {
        |            value=50000.0
        |        }
        |        house {
        |            value=1000000.0
        |        }
        |    }
        |    works {
        |        a=b
        |        b=c
        |    }
        |}
        |""".stripMargin).foldLeft(List[String]())((l, x) => if(!l.contains(x)) { l :+ x } else { l } )
    assert(substitutions.size == 1)
    assert(substitutions.head.equals("${v.age}"))
  }
}
