import evo_sim.core.SimulationLogic.worldUpdated
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.{Blob, Obstacle, Plant}
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

  //utility method
  def assertWorldEntitiesOfTypeTAreN[A](entitiesFilter: PartialFunction[SimulableEntity, A], size: Int) = {
    /*def worldEntitiesFiltered[A](entitiesFilter: PartialFunction[SimulableEntity, A]) =
      initialWorld.entities.collect(entitiesFilter)*/
    assert(
      initialWorld.entities.collect(entitiesFilter).size == size
    )
  }

  //test 1: check correct initialization of World upon Environment container chosen by user (so testing World's apply)
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

        it("should match the given Environment luminosity") {
          assert(initialWorld.luminosity == initialEnvironment.luminosity)
        }

        it("should match the amount of Blob specified"){
          assertWorldEntitiesOfTypeTAreN({ case e: Plant => e }, initialEnvironment.initialBlobNumber)
        }

        it("should match the amount of Plants specified"){
          assertWorldEntitiesOfTypeTAreN({ case e: Plant => e }, initialEnvironment.initialPlantNumber)
        }

        it("should match the amount of Obstacles specified"){
          assertWorldEntitiesOfTypeTAreN({ case e: Plant => e }, initialEnvironment.initialObstacleNumber)
        }

      }
    }
  }


  //2. testing worldUpdated
  val worldAfterOneIteration = worldUpdated(initialWorld)
  val worldAfterOneDay = worldUpdated(initialWorld)

  describe("A World") {
    describe("when updated"){
      it("should increase its current iteration") {
        assert(worldAfterOneIteration.currentIteration == initialWorld.currentIteration + 1)
      }

      it("should update its worldHistory") {
        assert(worldAfterOneIteration.worldHistory.contains(initialWorld))
      }
    }

    describe("after one day"){
      it("should return to initial temperature") {
        assert(worldAfterOneDay.temperature == initialWorld.temperature)
      }

      it("should return to initial luminosity") {
        assert(worldAfterOneDay.luminosity == initialWorld.luminosity)
      }
    }
  }




  //dynamic behaviours (variants or invariants):
  //test 2: check for correct luminosity day cycle (reset on every start of the day) and same value after one day

  //test 3: check for correct temperature day cycle (reset on every start of the day)

  //(at the endo of simulation, after given number of iteration
}
