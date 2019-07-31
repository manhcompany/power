package com.power.core.engine.stackmachine

import scala.collection.mutable

trait AbstractStackMachine {
  def execute[T](operators: Map[String, Seq[Operator[T]]]): mutable.Stack[Option[T]]
}
