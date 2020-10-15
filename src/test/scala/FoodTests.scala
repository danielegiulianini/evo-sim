import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityStructure.Blob
import evo_sim.model._
import org.scalatest.FunSuite

class FoodTests extends FunSuite {

  val blob: BaseBlob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    movementDirection = 0,
    stepToNextDirection = 15)
  val food: BaseFood = BaseFood(
    name = "food1",
    boundingBox = BoundingBox.Triangle.apply(point = Point2D(100, 100), height = 10),
    degradationEffect = DegradationEffect.foodDegradation,
    life = 100,
    effect = Effect.standardFoodEffect /*Effect.standardFoodEffect*/)

  test("BlobIncreasedLife") {
    val updatedBlob = blob.collided(food).toVector(0).asInstanceOf[Blob]
    assert(updatedBlob.life == blob.life + 10)
  }

  test("newBlob") {
    assert(blob.collided(food).size == 2)
  }
}

