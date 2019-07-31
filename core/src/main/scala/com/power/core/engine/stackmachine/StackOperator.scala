package com.power.core.engine.stackmachine

trait StackOperator[T] extends Operator[T] {
  val getNumberOfInputs: Int

  val execute: ExecuteType
}
