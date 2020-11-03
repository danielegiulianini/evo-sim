package evo_sim.model.world.dataProcessing

import evo_sim.model.entities.Entities.{BaseBlob, BaseFood}
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.EntityStructure.{Blob, Food}
import evo_sim.model.entities.entityStructure.effects.{CollisionEffect, DegradationEffect}
import evo_sim.model.entities.entityStructure.movement.{Direction, MovingStrategies}
import evo_sim.model.entities.entityStructure.{BoundingBox, EntityStructure, Point2D}
import evo_sim.model.world.dataProcessing.FinalStats.{dayValue, _}
import evo_sim.model.world.{Constants, World}
import org.scalatest.FunSpec

class FinalStatsTests extends FunSpec {

  private val blob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle(Point2D(100, 100), radius = Constants.DEF_BLOB_RADIUS),
    life = Constants.DEF_BLOB_LIFE,
    velocity = 20,
    degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
    fieldOfViewRadius = 30,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 10))

  private val blob2 = blob.copy(
    name = "blob2",
    velocity = 10,
    fieldOfViewRadius = 10)

  private val food = BaseFood(
    name = "food",
    boundingBox = BoundingBox.Triangle(point = Point2D(80, 100), height = 10),
    degradationEffect = DegradationEffect.standardDegradation,
    life = 100,
    collisionEffect = CollisionEffect.standardFoodEffect)

  private val food2 = food.copy(name = "food2")

  private val food3 = food.copy(name = "food3")

  private val entities = Set[SimulableEntity](blob, blob2, food, food2, food3)

  private val world: World = World(
    temperature = Constants.DEF_TEMPERATURE,
    luminosity = Constants.DEF_LUMINOSITY,
    width = Constants.WORLD_WIDTH,
    height = Constants.WORLD_HEIGHT,
    currentIteration = 0,
    entities = entities,
    totalIterations =  Constants.ITERATIONS_PER_DAY)

  def infiniteWorlds (world: World): Stream[World] = Stream.cons(world, infiniteWorlds(world.copy(currentIteration = world.currentIteration + 1)))

  val history: Stream[World] = infiniteWorlds(world).take(Constants.ITERATIONS_PER_DAY)

  describe("A final stat"){
    describe("which calculates the average of a specific value at the end of the day") {

      val expectedResultForBlob = List[Double](2, 2)
      val expectedResultForFood = List[Double](3, 3)

      it("must return the correct value") {
        assertResult(expectedResultForBlob)(dayValue(history)(entityDayQuantity(_.isInstanceOf[Blob])))
      }
      it("must return the correct value for the specific input") {
        assertResult(expectedResultForBlob)(dayValue(history)(entityDayQuantity(_.isInstanceOf[Blob])))
        assertResult(expectedResultForFood)(dayValue(history)(entityDayQuantity(_.isInstanceOf[Food])))
      }
    }

    describe("which calculates the average of a specific value during the day"){
      val expectedResultForVelocity = List[Double](15, 15)
      val expectedResultForFOV = List[Double](20, 20)

      it("must return the correct value") {
        assertResult(expectedResultForVelocity)(averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].velocity)))
      }
      it("must return the correct value for the specific input") {
        assertResult(expectedResultForVelocity)(averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].velocity)))
        assertResult(expectedResultForFOV)(averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].fieldOfViewRadius)))
      }
    }

    describe("which calculates the average of the whole simulation"){
      it("must return the correct value") {
        assertResult(2)(averageSimulation(history)(entityDayQuantity(_.isInstanceOf[Blob])))
      }
      it("must return the correct value for the specific input") {
        assertResult(2)(averageSimulation(history)(entityDayQuantity(_.isInstanceOf[Blob])))
        assertResult(3)(averageSimulation(history)(entityDayQuantity(_.isInstanceOf[Food])))
      }
    }
  }
}


