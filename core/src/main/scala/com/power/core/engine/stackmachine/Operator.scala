package com.power.core.engine.stackmachine

trait Operator[T] {
  type OperatorType = Seq[Option[T]] => Option[T]

  type ExecuteType = Seq[Option[T]] => Either[Option[String], Either[Option[T], Option[T]]]

  def getNumberOfInputs: Int

  val  execute: ExecuteType
}
