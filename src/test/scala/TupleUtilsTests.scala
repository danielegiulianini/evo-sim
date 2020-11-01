import org.scalatest.FunSpec

class TestTupleUtils extends FunSpec {

  //to be tested:
  //1. everyElementPairedWithOnlyOneOtherElement

  //2. implicit conversions:
    //1. contained for tuples
    //2. contained for set
    //3. contained for tupleset

  //3.containsAnyOf

  //test
  val testSet: Set[(Int,Int)] = Set((1, 2), (1, 3))
  val tupleSet = (0, 2)


  /*println("starting set:" + testSet)
  println("the set " + testSet +" contains any of: " + tupleSet + "?:" + containedAnyOf(testSet, tupleSet))
  println("resulting is :" + everyElementPairedWithOnlyOneOtherElement(testSet))

  val testSet2 = Set((1, 2), (1, 3), (3, 1))
  println("il set filtrato e': " + TupleUtils.everyElementPairedWithOnlyOneOtherElement(testSet2))*/

  describe("A ") {

  }
}
