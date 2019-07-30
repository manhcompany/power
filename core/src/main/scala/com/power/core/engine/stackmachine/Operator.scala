package com.power.core.engine.stackmachine

trait Operator[T] {
  type ExecuteType = Seq[Option[T]] => Either[Option[String], Option[List[T]]]
  def getNumberOfInputs: Int
  val  execute: ExecuteType
}
