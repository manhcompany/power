package com.power.core.engine.stackmachine

import scala.language.implicitConversions

object OperatorAdapter {
  /**
    * Convert NormalOperator to StackOperator
    * @param normalOperator NormalOperator
    * @tparam T type of StackMachine
    * @return StackOperator
    */
  implicit def normal2stack[T](normalOperator: NormalOperator[T]): StackOperator[T] = {
    new StackOperator[T] {
      override val getNumberOfInputs: Int = normalOperator.getNumberOfInputs
      override val execute: ExecuteType = operands => {
        val result = normalOperator.execute(operands)
        Right(Right(result))
      }
    }
  }

  /**
    * Convert BranchingOperator to StackOperator
    * @param branchingOperator BranchingOperator
    * @tparam T type of StackMachine
    * @return StackOperator
    */
  implicit def branch2stack[T](branchingOperator: BranchingOperator[T]): StackOperator[T] = {
    new StackOperator[T] {
      override val getNumberOfInputs: Int = branchingOperator.getNumberOfInputs
      override val execute: ExecuteType = operands => {
        branchingOperator.execute(operands)
      }
    }
  }

  implicit def operator2stack[T](operator: Operator[T]): StackOperator[T] = {
    new StackOperator[T] {
      override val getNumberOfInputs: Int = operator.getNumberOfInputs
      override val execute: this.ExecuteType = operands => {
        operator match {
          case value: NormalOperator[T] =>
            val result = value.execute(operands)
            Right(Right(result))
          case value: BranchingOperator[T] =>
            value.execute(operands)
        }
      }
    }
  }
}
