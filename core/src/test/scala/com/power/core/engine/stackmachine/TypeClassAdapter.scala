package com.power.core.engine.stackmachine

import org.scalatest.FlatSpec

class TypeClassAdapter extends FlatSpec {

  case class Celsius(degrees: Double)

  case class Fahrenheit(degrees: Double)

  trait ToCelsius[From] {
    def convert(source: From): Celsius
  }

  class FahrenheitConverter extends ToCelsius[Fahrenheit] {
    override def convert(source: Fahrenheit): Celsius = Celsius((source.degrees - 32) * 5 / 9)
  }

  class CelsiusIdentityConverter extends ToCelsius[Celsius] {
    override def convert(source: Celsius): Celsius = source
  }

  class AirConditioner {
    def setTemperature[T](degrees: T)(implicit ev: ToCelsius[T]): Unit = {
      val converter = implicitly[ToCelsius[T]]
      val asCelsius = converter.convert(degrees)
      println(s"Set to $asCelsius")
    }
  }



  it should "type class adapter" in {

    implicit val fahrenheitToCelsius = new FahrenheitConverter
    implicit val celsiusIdentity = new CelsiusIdentityConverter

    val airConditioner = new AirConditioner()
    airConditioner.setTemperature(new Fahrenheit(75))
    airConditioner.setTemperature(new Celsius(40))
  }
}
