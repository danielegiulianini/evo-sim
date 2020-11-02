import evo_sim.model.Entities.{BaseBlob, BaseFood}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Food
import evo_sim.model.effects.{CollisionEffect, DegradationEffect}
import evo_sim.model._
import org.scalatest.FunSpec

import scala.math.hypot

class MovementTests extends FunSpec {

  private val MOVE_TO_RIGHT_ANGLE = 0

  private val blob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle(Point2D(100, 100), radius = Constants.DEF_BLOB_RADIUS),
    life = Constants.DEF_BLOB_LIFE,
    velocity = Constants.DEF_BLOB_VELOCITY,
    degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
    fieldOfViewRadius = Constants.DEF_BLOB_FOV_RADIUS,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction.apply(MOVE_TO_RIGHT_ANGLE, 10))

  private val blobChangeDirection = blob.copy(
    name = "blob2",
    direction = Direction.apply(MOVE_TO_RIGHT_ANGLE, Constants.DEF_NEXT_DIRECTION))

  private val blobNearBorder = blob.copy(
    name = "blob3",
    boundingBox = BoundingBox.Circle(Point2D(Constants.WORLD_WIDTH, 100), radius = Constants.DEF_BLOB_RADIUS))

  private val food = BaseFood(
    name = "food",
    boundingBox = BoundingBox.Triangle(point = Point2D(80, 100), height = 10),
    degradationEffect = DegradationEffect.standardDegradation,
    life = 100,
    collisionEffect = CollisionEffect.standardFoodEffect)

  private val foodClosest = food.copy(
    name = "food2",
    boundingBox = BoundingBox.Triangle(point = Point2D(110, 100), height = 10)) //TODO

  private val entitiesWithoutFood = Set[SimulableEntity](blob, blobChangeDirection, blobNearBorder)

  private val entitiesWithFood = entitiesWithoutFood + food + foodClosest

  private val worldWithoutFood: World = World(
    temperature = Constants.DEF_TEMPERATURE,
    luminosity = Constants.DEF_LUMINOSITY,
    width = Constants.WORLD_WIDTH,
    height = Constants.WORLD_HEIGHT,
    currentIteration = 0,
    entities = entitiesWithoutFood,
    totalIterations = Constants.DEF_DAYS * Constants.ITERATIONS_PER_DAY)

  private val worldWithFood = worldWithoutFood.copy(entities = entitiesWithFood)

  describe("An Intelligent entity when it updates its position") {
    describe("with no eatable entities within the FOV") {
      it("has to move in a random direction"){
        val initialPosition = blob.boundingBox.point
        val newPosition = MovingStrategies.baseMovement(blob, worldWithoutFood, _.isInstanceOf[Food]).point
        assert(!initialPosition.equals(newPosition))
      }
      it("must assign a new value to stepToNextDirection if stepToNextDirection is 0") {
        val newStepToNextDirection = MovingStrategies.baseMovement(blobChangeDirection, worldWithoutFood, _.isInstanceOf[Food]).direction.stepToNextDirection
        assert(newStepToNextDirection > 0)
      }
      it("keeps the current direction and decreases the stepToNextDirection if stepToNextDirection is greater than 0"){
        val newDirection = MovingStrategies.baseMovement(blob, worldWithoutFood, _.isInstanceOf[Food]).direction
        assert(newDirection.stepToNextDirection > 0)
        assert(newDirection.angle equals blob.direction.angle)
      }
    }
    describe("with eatable entities within the FOV"){
      it("must go towards the closest entity"){
        val distanceBeforeClosestFood = distanceBetweenEntities(blob.boundingBox.point, foodClosest.boundingBox.point)
        val newPosition = MovingStrategies.baseMovement(blob, worldWithFood, _.isInstanceOf[Food]).point
        val distanceAfterClosestFood = distanceBetweenEntities(newPosition, foodClosest.boundingBox.point)
        val distanceAfterOtherFood = distanceBetweenEntities(newPosition, food.boundingBox.point)
        assert(distanceAfterClosestFood < distanceBeforeClosestFood)
        assert(distanceAfterClosestFood < distanceAfterOtherFood)
      }
      it("must return stepToNextDirection equals to 0"){
        val newStepToNextDirection = MovingStrategies.baseMovement(blob, worldWithFood, _.isInstanceOf[Food]).direction.stepToNextDirection
        assertResult(0)(newStepToNextDirection)
      }
    }
    it("must calculate the new position after changing direction if with the current direction the new position would have been out of bounds") {
      val newPosition = MovingStrategies.baseMovement(blobNearBorder, worldWithoutFood, _.isInstanceOf[Food])
      assert(!newPosition.direction.angle.equals(blobNearBorder.direction.angle))
      assert((0 to worldWithoutFood.width contains newPosition.point.x) && (0 to worldWithoutFood.height contains newPosition.point.y))
    }
  }

  private def distanceBetweenEntities(a: Point2D, b: Point2D): Double = {
    hypot(b.x - a.x, b.y - a.y)
  }

}
