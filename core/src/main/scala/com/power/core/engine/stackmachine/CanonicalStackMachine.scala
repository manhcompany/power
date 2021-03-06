package com.power.core.engine.stackmachine

import com.power.core.util.Logging

import scala.collection.mutable

object CanonicalStackMachine extends AbstractStackMachine with Logging {

  /**
    * Execute a branch that includes operators
    * @param operators Operators of branch that is executed
    * @param stack A mutable Stack
    * @param branches Set of branches
    * @tparam T Type of elements in stack machine
    * @return Stack
    */
  def executeBranch[T](operators: Seq[StackOperator[T]], stack: mutable.Stack[Option[T]], branches: Map[String, Seq[StackOperator[T]]]): mutable.Stack[Option[T]] = {
    assert(operators.nonEmpty, "operators should be non empty")
    assert(if (operators != null) true else false, "operators should be not null")
    operators.foldLeft(stack)((s, op) => {
      val operands = (1 to op.getNumberOfInputs).toList.map(_ => s.pop())
      op.execute(operands) match {
        case Right(optionDataFrames) => optionDataFrames match {
          case Right(dfs) => dfs match {
            case Some(dfs) => dfs.foldLeft(s)((ss, df) => ss.push(Some(df)))
            case None => s
          }
          case Left(_) => s.push(None)
        }
        case Left(label) => label match {
          case Some(labelStr) => executeBranch(branches(labelStr), stack, branches)
          case None => s
        }
      }
    })
  }

  /**
    * Start the stack machine
    * @param branches Operators are grouped by label. Each group as a branch include branch name (label) and operators
    * @tparam T Type of elements in stack machine. Example: Int, Long, String, DataFrame
    */
  override def execute[T](branches: Map[String, Seq[StackOperator[T]]]): mutable.Stack[Option[T]] = {
    assert(branches.keySet.contains("main"), "branches should contains main")
    log.info("Start stack machine!")
    val stack = mutable.Stack[Option[T]]()
    executeBranch[T](branches("main"), stack, branches)
  }
}
