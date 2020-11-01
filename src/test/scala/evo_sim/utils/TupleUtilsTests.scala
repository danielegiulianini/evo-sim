import evo_sim.utils.Queriable.containedAnyOf
import evo_sim.utils.QueriableImplicits.ContainsForSet
import evo_sim.utils.TupleUtils.everyElementPairedWithOnlyOneOtherElement
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
  val setTestSet = Set(1, 2, 3)
  val tuple2TestSet = (0, 2)
  val setOfTuple2TestSet: Set[(Int,Int)] = Set((1, 2), (0, 3))

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

  describe("A Set") {
    describe("initialized with some values and not updated"){
      describe("when compared to a pair of values") {
        describe("when it contains one of them") {
          it("should contain any of the values of the pair") {
            val valuesOneOfWhichIsContainedInTestSet = (1, 9)
            assert(containedAnyOf(setTestSet, valuesOneOfWhichIsContainedInTestSet))
          }
        }
        describe("when it contains all of them") {
          val valuesOfTestSetAsTuple = (1, 3)
          it("should contain any of the values of the pair") {
            assert(containedAnyOf(setTestSet, valuesOfTestSetAsTuple))
          }
        }
        describe("when it contains none of them") {
          val valuesNotContainedInTestSet = (1, 3)
          it("should not contain any of the values of the pair") {
            assert(containedAnyOf(setTestSet, valuesNotContainedInTestSet))
          }
        }
      }
    }
  }

  //missing tests for containedAnyOf for tuple ... but it is the same implementation

  val everyElementNOTPairedWithOnlyOneOtherElement = Set((1, 2), (1, 3), (3, 1))

  val setOfTuple2WithEveryElementPairedWithOnlyOneOtherElement1 = Set(1, 2)
  val setOfTuple2WithEveryElementPairedWithOnlyOneOtherElement2 = Set(1, 3)
  val setOfTuple2WithEveryElementPairedWithOnlyOneOtherElement3 = Set(3, 1)

  describe("A Set of Tuples") {
    describe("where a tuple element is paired with more than one other element"){
      describe("when every tuple element must be paired with only one other element") {
        it("should remove as to become a set where a tuple element is paired with more than one other element") {

          assert(
            everyElementPairedWithOnlyOneOtherElement(everyElementNOTPairedWithOnlyOneOtherElement) ==
              setOfTuple2WithEveryElementPairedWithOnlyOneOtherElement1 ||
              everyElementPairedWithOnlyOneOtherElement(everyElementNOTPairedWithOnlyOneOtherElement) ==
                setOfTuple2WithEveryElementPairedWithOnlyOneOtherElement2 ||
              everyElementPairedWithOnlyOneOtherElement(everyElementNOTPairedWithOnlyOneOtherElement) ==
                setOfTuple2WithEveryElementPairedWithOnlyOneOtherElement3
          )
        }
      }


    }
  }
  describe("where a tuple element is paired with just one other element") {
    describe("when every tuple element must be paired with only one other element") {
      it("should remain as it is") {
        assert(everyElementPairedWithOnlyOneOtherElement(setOfTuple2WithEveryElementPairedWithOnlyOneOtherElement1)
          == setOfTuple2WithEveryElementPairedWithOnlyOneOtherElement1)
      }
    }
  }
}