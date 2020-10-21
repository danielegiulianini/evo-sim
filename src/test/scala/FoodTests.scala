import evo_sim.model.Entities.{BaseBlob, BaseFood, PoisonBlob}
import evo_sim.model.EntityStructure.Blob
import evo_sim.model._
import evo_sim.model.Constants._
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
    direction = Direction(0, 15)
    /*movementDirection = 0,
    stepToNextDirection = 15*/)
  val poisonBlob: PoisonBlob = PoisonBlob(
    name = "blob2",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15),
    cooldown = Constants.DEF_COOLDOWN)

  val standardFood: BaseFood = BaseFood(
    name = "food1",
    boundingBox = BoundingBox.Triangle.apply(point = Point2D(100, 100), height = 10),
    degradationEffect = DegradationEffect.foodDegradation,
    life = 100,
    effect = Effect.standardFoodEffect /*Effect.standardFoodEffect*/)
  val reproducingFood: BaseFood = BaseFood(
    name = "food2",
    boundingBox = BoundingBox.Triangle.apply(point = Point2D(100, 100), height = 10),
    degradationEffect = DegradationEffect.foodDegradation,
    life = 100,
    effect = Effect.reproduceBlobFoodEffect /*Effect.standardFoodEffect*/)

  test("BlobIncreasedLife") {
    val updatedBlob = blob.collided(standardFood).toVector(0).asInstanceOf[Blob]
    assert(updatedBlob.life == blob.life + DEF_FOOD_ENERGY)
    assert(blob.collided(reproducingFood).exists(b => b.asInstanceOf[Blob].life == DEF_BLOB_LIFE))
  }

  test("newBlob") {
    assert(blob.collided(standardFood).size == 1)
    assert(blob.collided(reproducingFood).size == 2)
    assert(poisonBlob.collided(reproducingFood).size == 1)
  }
}

