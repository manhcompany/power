package com.power.core.zippers

import org.scalatest.FlatSpec

class IterableTest extends FlatSpec{
  class ListTest extends Iterable[Int] {
    override def iterator: Iterator[Int] = {
      Iterator[Int](1, 2, 3, 4, 5)
    }
  }

  it should "filter" in {
    val a = new ListTest()
    a.filter(x => x == 5).foreach(x => println(x))
    a.toList.toIterator
  }
}
