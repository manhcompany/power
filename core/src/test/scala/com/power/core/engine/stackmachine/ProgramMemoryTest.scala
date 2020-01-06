package com.power.core.engine.stackmachine

import org.scalatest.FlatSpec

class ProgramMemoryTest extends FlatSpec{
  behavior of "ProgramMemoryTest"

  it should "Load and Store Operator" in {
    val memory = new ProgramMemory[Int]()
    val store = memory.StoreOperator("a")
    assert(store.execute(Seq(Some(1))).isEmpty)

    val load = memory.LoadOperator("a")
    assert(load.execute(Seq()).contains(1))
  }
}
