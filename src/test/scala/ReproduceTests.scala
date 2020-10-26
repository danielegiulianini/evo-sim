import evo_sim.model.Entities.BaseBlob
import evo_sim.model.{BoundingBox, DegradationEffect, Direction, GenderValue, MovingStrategies, Point2D}
import org.scalatest.FunSuite

class ReproduceTests extends FunSuite {

  val blobM: BaseBlob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    gender = GenderValue.Male,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))
  val blobF: BaseBlob = BaseBlob(
    name = "blob2",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    gender = GenderValue.Female,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))
  val blobGenderless: BaseBlob = BaseBlob(
    name = "blob2",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    gender = GenderValue.Genderless,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))

  test("newBlob") {
    assert(blobM.collided(blobF).size == 1)
    assert(blobF.collided(blobM).size == 2)
    assert(blobM.collided(blobGenderless).size == 1)
    assert(blobF.collided(blobGenderless).size == 1)
    assert(blobGenderless.collided(blobM).size == 1)
    assert(blobGenderless.collided(blobF).size == 1)
  }

}
