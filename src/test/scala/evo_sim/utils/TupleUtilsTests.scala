import org.scalatest.FunSpec

class TestTupleUtils extends FunSpec {

  //to be tested:
  //1. everyElementPairedWithOnlyOneOtherElement

  //2. implicit conversions:
  //1. contained for tuple2
  //2. contained for set
  //3. contained for tuple2set

  //3.containsAnyOf

  //test sets
  val tuple2TestSet = (0, 2)
  val setOfTuple2TestSet: Set[(Int,Int)] = Set((1, 2), (0, 3))


  /*println("starting set:" + testSet)
  println("the set " + testSet +" contains any of: " + tupleSet + "?:" + containedAnyOf(testSet, tupleSet))
  println("resulting is :" + everyElementPairedWithOnlyOneOtherElement(testSet))

  val testSet2 = Set((1, 2), (1, 3), (3, 1))
  println("il set filtrato e': " + TupleUtils.everyElementPairedWithOnlyOneOtherElement(testSet2))*/

  describe("A tuple2") {
    describe("initialized with 2 values and not updated"){
      it("should contain these values") {
        assert(
          evo_sim.utils.QueriableImplicits.ContainsForHomogeneousTuple2.contained(tuple2TestSet, 0)
            &&
            evo_sim.utils.QueriableImplicits.ContainsForHomogeneousTuple2.contained(tuple2TestSet, 2))

      }
      it("should not contain values different to those values") {
        assert(!evo_sim.utils.QueriableImplicits.ContainsForHomogeneousTuple2.contained(tuple2TestSet, 1))
      }
    }
  }


}