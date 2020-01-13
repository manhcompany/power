package com.power.core.engine.stackmachine

trait Operator[T] {
  type NormalOperatorType = Seq[Option[T]] => Option[T]

  type BranchOperatorType = Seq[Option[T]] => Either[Option[String], Either[Option[T], Option[Seq[T]]]]

  type ExecuteType = Seq[Option[T]] => Either[Option[String], Either[Option[T], Option[Seq[T]]]]

  val getNumberOfInputs: Int
}