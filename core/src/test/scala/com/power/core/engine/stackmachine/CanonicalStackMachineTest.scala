package com.power.core.engine.stackmachine

import org.scalatest.{BeforeAndAfterEach, FlatSpec}

import scala.collection.mutable

class CanonicalStackMachineTest extends FlatSpec with BeforeAndAfterEach {

  override def beforeEach() {

  }

  override def afterEach() {

  }

  case class IntOperand(number: Int) extends Operator[Int] {
    override def getNumberOfInputs: Int = 0

    override val execute: ExecuteType = operands => {
      Right(Right(Some(number)))
    }
  }

  case class Add() extends Operator[Int] {
    override def getNumberOfInputs: Int = 2

    override val execute: ExecuteType = operands => {
      val a = operands.head
      val b = operands.tail.head
      Right(Right(Some(a.get + b.get)))
    }
  }

  case class Minus() extends Operator[Int] {
    override def getNumberOfInputs: Int = 2

    override val execute: ExecuteType = operands => {
      val a = operands.head
      val b = operands.tail.head
      Right(Right(Some(b.get - a.get)))
    }
  }

  case class PrintOperand() extends Operator[Int] {
    override def getNumberOfInputs: Int = 1

    override val execute: ExecuteType = operands => {
      val a = operands.head
      println(a.get)
      Right(Right(None))
    }
  }

  behavior of "CanonicalStackMachineTest"

  it should "executeBranch is success" in {
    val operators = Seq[Operator[Int]](IntOperand(3), IntOperand(4), Add(), IntOperand(5), Minus(), PrintOperand())
    val branches = Map[String, Seq[Operator[Int]]](("main", operators))
    val stack = mutable.Stack[Option[Int]]()
    CanonicalStackMachine.executeBranch(operators, stack, branches)
  }

  it should "execute" in {
    val operators = Seq[Operator[Int]](IntOperand(3), IntOperand(4), Add(), IntOperand(5), Minus(), PrintOperand())
    val branches = Map[String, Seq[Operator[Int]]](("main", operators))
    CanonicalStackMachine.execute(branches)
  }

}
