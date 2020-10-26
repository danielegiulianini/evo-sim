import evo_sim.core.TupleUtils
import evo_sim.core.TupleUtils.{containedAnyOf, everyElementPairedWithOnlyOneOtherElement}

class TestTupleUtils {
  //test
  val testSet: Set[(Int,Int)]= Set((1, 2), (1, 3))
  val tupleSet = (0, 2)
  println("starting set:" + testSet)
  println("the set " + testSet +" contains any of: " + tupleSet + "?:" + containedAnyOf(testSet, tupleSet))
  println("resulting is :" + everyElementPairedWithOnlyOneOtherElement(testSet))

  val testSet2 = Set((1, 2), (1, 3), (3, 1))
  println("il set filtrato e': " + TupleUtils.everyElementPairedWithOnlyOneOtherElement(testSet2))
}
