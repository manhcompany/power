package com.power.core.engine.stackmachine

trait NormalOperator[T] extends Operator[T] {
  val getNumberOfInputs: Int

  val execute: NormalOperatorType
}
