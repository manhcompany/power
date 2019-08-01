package com.power.core.engine.stackmachine

object OperatorAdapter {
  implicit def normal2stack[T](normalOperator: NormalOperator[T]): StackOperator[T] = {
    new StackOperator[T] {
      override val getNumberOfInputs: Int = normalOperator.getNumberOfInputs
      override val execute: ExecuteType = operands => {
        val result = normalOperator.execute(operands)
        Right(Right(result))
      }
    }
  }

  implicit def branch2stack[T](branchingOperator: BranchingOperator[T]): StackOperator[T] = {
    new StackOperator[T] {
      override val getNumberOfInputs: Int = branchingOperator.getNumberOfInputs
      override val execute: ExecuteType = operands => {
        branchingOperator.execute(operands)
      }
    }
  }
}
