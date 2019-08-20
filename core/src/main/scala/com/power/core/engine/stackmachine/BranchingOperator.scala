package com.power.core.engine.stackmachine

trait BranchingOperator[T] extends Operator[T] {
  val getNumberOfInputs: Int

  val execute: BranchOperatorType
}