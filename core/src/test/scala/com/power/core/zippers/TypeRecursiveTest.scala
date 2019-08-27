package com.power.core.zippers

import org.scalatest.FlatSpec

class TypeRecursiveTest extends FlatSpec{
  behavior of "TypeRecursiveTest"



  it should "OK" in {

    trait Recurse {
      type Next <: Recurse
      type X[R <: Recurse] <: Int
    }

    trait RecurseA extends Recurse {
      type Next = RecurseA
      type X[R <: Recurse] = R # X[R # Next]
    }
  }

  it should "type recursive" in {
    trait Recurse {
      type Next <: Recurse
      type X[R <: Recurse] <: Int
    }

    trait RecurseA extends Recurse {
      type Next = RecurseA
      type X[R <: Recurse] = R#X[R#Next]
    }
  }

  it should "test" in {
    trait Recurse {
      type X[A, B] <: A
    }

    type A = Int
    val a: A = 10
  }


}

// X[RecurseA] => X[RecurseA] => X[RecurseA]

// X[RecurseA] => Int