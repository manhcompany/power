package com.power.core.engine.stackmachine

import org.scalatest.FlatSpec

class AdapterTest extends FlatSpec {
  case class Celsius(degrees: Double)
  case class Fahrenheit(degees: Double)

  class AirConditioner {
    def setTemperature(celsius: Celsius) = println(s"Set to ${celsius.degrees}")
  }

  implicit def fahrenheit2celsius(fahrenheit: Fahrenheit): Celsius = new Celsius((fahrenheit.degees - 32) * 5 / 9)

  it should "set temperature" in {
    new AirConditioner().setTemperature(new Fahrenheit(32))
  }
}
