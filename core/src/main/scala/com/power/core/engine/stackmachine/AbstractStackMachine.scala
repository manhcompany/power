package com.power.core.engine.stackmachine

import scala.collection.mutable

trait AbstractStackMachine {
  def execute[T](operators: Map[String, Seq[StackOperator[T]]]): mutable.Stack[Option[T]]
}
