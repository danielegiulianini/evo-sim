package evo_sim.model

import evo_sim.model.entities.Entities.{BaseBlob, BaseFood}
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.EntityStructure.Food
import evo_sim.model.entities.entityStructure.effects.{CollisionEffect, DegradationEffect}
import evo_sim.model.entities.entityStructure.movement.DemonstrationCompletelyPrologMovement.completelyPrologBaseMovement
import evo_sim.model.entities.entityStructure.movement.DemonstrationCompletelyScalaMovement.completelyScalaBaseMovement
import evo_sim.model.entities.entityStructure.movement.MovingStrategies.baseMovement
import evo_sim.model.entities.entityStructure.movement.{Direction, MovingStrategies}
import evo_sim.model.entities.entityStructure.{BoundingBox, EntityStructure, Point2D}
import evo_sim.model.world.{Constants, World}
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
    direction = Direction(MOVE_TO_RIGHT_ANGLE, 10))

  private val blobChangeDirection = blob.copy(
    name = "blob2",
    direction = Direction(MOVE_TO_RIGHT_ANGLE, Constants.DEF_NEXT_DIRECTION))

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
    boundingBox = BoundingBox.Triangle(point = Point2D(110, 100), height = 10))

  private val entitiesWithoutFood = Set[SimulableEntity](blob, blobChangeDirection, blobNearBorder)

  private val entitiesWithFood = entitiesWithoutFood + food + foodClosest

  private val worldWithoutFood: World = world.World(
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
        val newPositionStandardMov = baseMovement(blob, worldWithoutFood, _.isInstanceOf[Food]).point
        val newPositionAllScalaMov = completelyScalaBaseMovement(blob, worldWithoutFood, _.isInstanceOf[Food]).point
        val newPositionAllPrologMov = completelyPrologBaseMovement(blob, worldWithoutFood, _.isInstanceOf[Food]).point
        assert(!initialPosition.equals(newPositionStandardMov))
        assert(!initialPosition.equals(newPositionAllScalaMov))
        assert(!initialPosition.equals(newPositionAllPrologMov))

      }
      it("must assign a new value to stepToNextDirection if stepToNextDirection is 0") {
        val newStepToNextDirectionStandardMov = baseMovement(blobChangeDirection, worldWithoutFood, _.isInstanceOf[Food]).direction.stepToNextDirection
        val newStepToNextDirectionScalaMov = completelyScalaBaseMovement(blobChangeDirection, worldWithoutFood, _.isInstanceOf[Food]).direction.stepToNextDirection
        val newStepToNextDirectionPrologMov = completelyPrologBaseMovement(blobChangeDirection, worldWithoutFood, _.isInstanceOf[Food]).direction.stepToNextDirection
        assert(newStepToNextDirectionStandardMov > 0)
        assert(newStepToNextDirectionScalaMov > 0)
        assert(newStepToNextDirectionPrologMov > 0)
      }
      it("keeps the current direction and decreases the stepToNextDirection if stepToNextDirection is greater than 0"){
        val newDirectionStandardMov = baseMovement(blob, worldWithoutFood, _.isInstanceOf[Food]).direction
        val newDirectionScalaMov = completelyScalaBaseMovement(blob, worldWithoutFood, _.isInstanceOf[Food]).direction
        val newDirectionPrologMov = completelyPrologBaseMovement(blob, worldWithoutFood, _.isInstanceOf[Food]).direction
        assert(newDirectionStandardMov.stepToNextDirection > 0)
        assert(newDirectionStandardMov.angle equals blob.direction.angle)
        assert(newDirectionScalaMov.stepToNextDirection > 0)
        assert(newDirectionScalaMov.angle equals blob.direction.angle)
        assert(newDirectionPrologMov.stepToNextDirection > 0)
        assert(newDirectionPrologMov.angle equals blob.direction.angle)
      }
    }
    describe("with eatable entities within the FOV"){
      it("must go towards the closest entity"){

        val distanceBeforeClosestFood = distanceBetweenEntities(blob.boundingBox.point, foodClosest.boundingBox.point)

        //StandardMov
        val newPositionStandardMov = baseMovement(blob, worldWithFood, _.isInstanceOf[Food]).point
        val distanceAfterClosestFoodStandardMov = distanceBetweenEntities(newPositionStandardMov, foodClosest.boundingBox.point)
        val distanceAfterOtherFoodStandardMov = distanceBetweenEntities(newPositionStandardMov, food.boundingBox.point)
        assert(distanceAfterClosestFoodStandardMov < distanceBeforeClosestFood)
        assert(distanceAfterClosestFoodStandardMov < distanceAfterOtherFoodStandardMov)

        //ScalaMov
        val newPositionScalaMov = completelyScalaBaseMovement(blob, worldWithFood, _.isInstanceOf[Food]).point
        val distanceAfterClosestFoodScalaMov = distanceBetweenEntities(newPositionScalaMov, foodClosest.boundingBox.point)
        val distanceAfterOtherFoodScalaMov = distanceBetweenEntities(newPositionScalaMov, food.boundingBox.point)
        assert(distanceAfterClosestFoodScalaMov < distanceBeforeClosestFood)
        assert(distanceAfterClosestFoodScalaMov < distanceAfterOtherFoodScalaMov)

        //PrologMov
        val newPositionPrologMov = completelyPrologBaseMovement(blob, worldWithFood, _.isInstanceOf[Food]).point
        val distanceAfterClosestFoodPrologMov = distanceBetweenEntities(newPositionPrologMov, foodClosest.boundingBox.point)
        val distanceAfterOtherFoodPrologMov = distanceBetweenEntities(newPositionPrologMov, food.boundingBox.point)
        assert(distanceAfterClosestFoodPrologMov < distanceBeforeClosestFood)
        assert(distanceAfterClosestFoodPrologMov < distanceAfterOtherFoodPrologMov)
      }
      it("must return stepToNextDirection equals to 0"){
        val newStepToNextDirectionStandardMov = baseMovement(blob, worldWithFood, _.isInstanceOf[Food]).direction.stepToNextDirection
        val newStepToNextDirectionScalaMov = completelyScalaBaseMovement(blob, worldWithFood, _.isInstanceOf[Food]).direction.stepToNextDirection
        val newStepToNextDirectionPrologMov = completelyPrologBaseMovement(blob, worldWithFood, _.isInstanceOf[Food]).direction.stepToNextDirection
        assertResult(0)(newStepToNextDirectionStandardMov)
        assertResult(0)(newStepToNextDirectionScalaMov)
        assertResult(0)(newStepToNextDirectionPrologMov)
      }
    }
    it("must calculate the new position after changing direction if with the current direction the new position would have been out of bounds") {
      val newPositionStandardMov = baseMovement(blobNearBorder, worldWithoutFood, _.isInstanceOf[Food])
      val newPositionScalaMov = completelyScalaBaseMovement(blobNearBorder, worldWithoutFood, _.isInstanceOf[Food])
      val newPositionPrologMov = completelyPrologBaseMovement(blobNearBorder, worldWithoutFood, _.isInstanceOf[Food])

      //StandardMov
      assert(!newPositionStandardMov.direction.angle.equals(blobNearBorder.direction.angle))
      assert(0 to worldWithoutFood.width contains newPositionStandardMov.point.x)
      assert(0 to worldWithoutFood.height contains newPositionStandardMov.point.y)

      //ScalaMov
      assert(!newPositionScalaMov.direction.angle.equals(blobNearBorder.direction.angle))
      assert(0 to worldWithoutFood.width contains newPositionScalaMov.point.x)
      assert(0 to worldWithoutFood.height contains newPositionScalaMov.point.y)

      //PrologMov
      assert(!newPositionPrologMov.direction.angle.equals(blobNearBorder.direction.angle))
      assert(0 to worldWithoutFood.width contains newPositionPrologMov.point.x)
      assert(0 to worldWithoutFood.height contains newPositionPrologMov.point.y)
    }
  }

  private def distanceBetweenEntities(a: Point2D, b: Point2D): Double = {
    hypot(b.x - a.x, b.y - a.y)
  }

}
