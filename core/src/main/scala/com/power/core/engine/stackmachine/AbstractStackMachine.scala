package com.power.core.engine.stackmachine

trait AbstractStackMachine {
  def execute[T](operators: Map[String, Seq[Operator[T]]]): Unit
}
