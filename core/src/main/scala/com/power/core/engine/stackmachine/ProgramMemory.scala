package com.power.core.engine.stackmachine

import scala.collection.mutable

class ProgramMemory[T] {
  private val memory: mutable.Map[String, T] = mutable.Map[String, T]()

  class LoadOperator(name: String) extends NormalOperator[T] {
    override val getNumberOfInputs: Int = 0
    override val execute: NormalOperatorType = _ => {
      memory.get(name)
    }
  }

  class StoreOperator(name: String) extends NormalOperator[T] {
    override val getNumberOfInputs: Int = 1
    override val execute: NormalOperatorType = operands => {
      memory.update(name, operands.head.get)
      None
    }
  }
}
