import evo_sim.model.{Environment, World}
import org.scalatest.FunSpec

class WorldTests extends FunSpec {

  val initialEnvironment : Environment =
    Environment(
      temperature = 2,
      luminosity = 3,
      initialBlobNumber = 10,
      initialPlantNumber = 3,
      initialObstacleNumber = 3,
      daysNumber = 5)

  val initialWorld = World(initialEnvironment)

  //test 1: check correct initialization of World upon Environment container chosen by user
  describe("A World") {
    describe("when initialized") {
      it("should have empty history") {
        assert(initialWorld.worldHistory.isEmpty)
      }

      it("should be at 0 currentIteration") {
        assert(initialWorld.currentIteration == 0)
      }

      describe("with a given Environment") {
        it("should match the given Environment temperature") {
          assert(initialWorld.temperature == initialEnvironment.temperature)
        }
      }
    }
  }

  //dynamic behaviours (variants or invariants):
  //test 2: check for correct luminosity day cycle (reset on every start of the day)

  //test 3: check for correct temperature day cycle (reset on every start of the day)

  //test 1: check correct iteration count progress

  //(at the endo of simulation, after given number of iteration
}
