package evo_sim.model

import evo_sim.core.SimulationLogic.worldUpdated
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.EntityStructure.{Blob, Obstacle, Plant}
import evo_sim.model.world.{Constants, Environment, World}
import evo_sim.utils.TestUtils._
import org.scalatest.{Assertion, FunSpec}

class WorldTests extends FunSpec {

  val initialEnvironment : Environment =
    Environment(
      temperature = 2,
      luminosity = 3,
      initialBlobNumber = 10,
      initialPlantNumber = 3,
      initialObstacleNumber = 3,
      daysNumber = 5)

  val initialWorld: World = World(initialEnvironment)

  //utility method
  def assertWorldEntitiesOfTypeTAreN[A](entitiesFilter: PartialFunction[SimulableEntity, A], size: Int): Assertion = {
    assert(
      initialWorld.entities.collect(entitiesFilter).size == size
    )
  }

  //test 1: check correct initialization of World upon Environment container chosen by user (ie testing World's apply)
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
          assertWorldEntitiesOfTypeTAreN({ case e: Blob => e }, initialEnvironment.initialBlobNumber)
        }

        it("should match the amount of Plants specified"){
          assertWorldEntitiesOfTypeTAreN({ case e: Plant => e }, initialEnvironment.initialPlantNumber)
        }

        it("should match the amount of Obstacles specified"){
          assertWorldEntitiesOfTypeTAreN({ case e: Obstacle => e }, initialEnvironment.initialObstacleNumber)
        }

      }
    }
  }


  //2. testing worldUpdated
  val worldAfterOneIteration: World = worldUpdated(initialWorld)
  val worldAfterOneDay: World = chain(Constants.ITERATIONS_PER_DAY)(initialWorld)(worldUpdated)

  describe("A World") {

    //test 2: check for correct luminosity day cycle (reset on every start of the day) and same value after one day
    describe("when updated"){
      it("should increase its current iteration") {
        assert(worldAfterOneIteration.currentIteration == initialWorld.currentIteration + 1)
      }

      it("should update its worldHistory") {
        assert(worldAfterOneIteration.worldHistory.contains(initialWorld))
      }
    }

    //test 3: check for correct temperature day cycle (reset on every start of the day)
    describe("after one day"){
      it("should return to initial temperature") {
        assert(worldAfterOneDay.temperature == initialWorld.temperature)
      }

      it("should return to initial luminosity") {
        assert(worldAfterOneDay.luminosity == initialWorld.luminosity)
      }
    }
  }
}
