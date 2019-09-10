package com.power.spark.apps

import org.scalatest.FlatSpec

class ETLTest extends FlatSpec {

  behavior of "ETLTest"

  it should "execute" in {
    ETL.execute()
  }
}
