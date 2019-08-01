package com.power.core.engine.stackmachine

import org.scalatest.{BeforeAndAfterEach, FlatSpec}

import scala.collection.mutable

import OperatorAdapter.branch2stack
import OperatorAdapter.normal2stack

class CanonicalStackMachineTest extends FlatSpec with BeforeAndAfterEach {

  override def beforeEach() {

  }

  override def afterEach() {

  }

  case class IntOperand(number: Int) extends NormalOperator[Int] {
    override val getNumberOfInputs: Int = 0

    override val execute: NormalOperatorType = operands => {
      Some(number)
    }
  }

  case class Add() extends NormalOperator[Int] {
    override val getNumberOfInputs: Int = 2

    override val execute: NormalOperatorType = operands => {
      val a = operands.head
      val b = operands.tail.head
      Some(a.get + b.get)
    }
  }

  case class Minus() extends NormalOperator[Int] {
    override val getNumberOfInputs: Int = 2

    override val execute: NormalOperatorType = operands => {
      val a = operands.head
      val b = operands.tail.head
      Some(b.get - a.get)
    }
  }

  case class PrintOperand() extends NormalOperator[Int] {
    override val getNumberOfInputs: Int = 1

    override val execute: NormalOperatorType = operands => {
      val a = operands.head
      println(a.get)
      None
    }
  }

  case class CheckGreaterThanZero() extends BranchingOperator[Int] {
    override val getNumberOfInputs: Int = 1

    override val execute: BranchOperatorType = operands => {
      val a = operands.head
      if(a.get > 0) Right(Left(None)) else Right(Right(a))
    }
  }

  case class CheckLessThanZero() extends BranchingOperator[Int] {
    override val getNumberOfInputs: Int = 1

    override val execute: ExecuteType = operands => {
      val a = operands.head
      if(a.get < 0) Right(Left(None)) else Right(Right(a))
    }
  }

  case class BranchTwoWayOperator(left: String, right: String) extends BranchingOperator[Int] {
    override val getNumberOfInputs: Int = 1

    override val execute: ExecuteType = operands => {
      operands.head match {
        case Some(_) => Left(Some(left))
        case None => Left(Some(right))
      }
    }
  }

  case class BranchOperator(branch: String) extends BranchingOperator[Int] {
    override val getNumberOfInputs: Int = 1

    override val execute: ExecuteType = operands => {
      operands.head match {
        case Some(_) => Left(None)
        case None => Left(Some(branch))
      }
    }
  }

//  implicit def normal2stack[T](normalOperator: NormalOperator[T]): StackOperator[T] = {
//    new StackOperator[T] {
//      override val getNumberOfInputs: Int = normalOperator.getNumberOfInputs
//      override val execute: ExecuteType = operands => {
//        val result = normalOperator.execute(operands)
//        Right(Right(result))
//      }
//    }
//  }
//
//  implicit def branch2stack[T](branchingOperator: BranchingOperator[T]): StackOperator[T] = {
//    new StackOperator[T] {
//      override val getNumberOfInputs: Int = branchingOperator.getNumberOfInputs
//      override val execute: ExecuteType = operands => {
//        branchingOperator.execute(operands)
//      }
//    }
//  }

  behavior of "CanonicalStackMachineTest"

  it should "executeBranch is success" in {
    val operators = Seq[StackOperator[Int]](IntOperand(3), IntOperand(4), Add(), IntOperand(5), Minus(), PrintOperand(), IntOperand(2), IntOperand(4), Add(), PrintOperand())
    val branches = Map[String, Seq[StackOperator[Int]]](("main", operators))
    val stack = mutable.Stack[Option[Int]]()
    CanonicalStackMachine.executeBranch(operators, stack, branches)
  }

  it should "execute" in {
    val operators = Seq[StackOperator[Int]](IntOperand(3), IntOperand(4), Add(), IntOperand(5), Minus(), PrintOperand())
    val branches = Map[String, Seq[StackOperator[Int]]](("main", operators))
    CanonicalStackMachine.execute(branches)
  }

  it should "execute with two way branching" in {
    val operators = Seq[StackOperator[Int]](IntOperand(3), IntOperand(4), Add(), IntOperand(5), Minus(), CheckGreaterThanZero(), BranchTwoWayOperator("left", "right"))
    val left = Seq[StackOperator[Int]](IntOperand(2), IntOperand(4), Add(), PrintOperand())
    val right = Seq[StackOperator[Int]](IntOperand(2), IntOperand(10), Add(), PrintOperand())
    val branches = Map[String, Seq[StackOperator[Int]]](("main", operators), ("left", left), ("right", right))
    CanonicalStackMachine.execute(branches)
  }

  it should "execute with one way branching" in {
    val operators = Seq[StackOperator[Int]](IntOperand(3), IntOperand(4), Add(), IntOperand(5), Minus(), CheckGreaterThanZero(), BranchOperator("left"), IntOperand(30), IntOperand(4), Add(), PrintOperand())
    val left = Seq[StackOperator[Int]](IntOperand(2), IntOperand(4), Add(), PrintOperand())
    val branches = Map[String, Seq[StackOperator[Int]]](("main", operators), ("left", left))
    CanonicalStackMachine.execute(branches)
  }
}
