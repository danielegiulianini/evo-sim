import evo_sim.model.Entities.{BaseBlob, BaseObstacle}
import evo_sim.model.EntityStructure.Blob
import evo_sim.model._
import evo_sim.model.Utils.chain
import org.scalatest.FunSuite

class ObstacleTests extends FunSuite {

  val INIT_BLOB_LIFE = 100
  val INIT_BLOB_VEL = 50

  val blob: BaseBlob = BaseBlob(
    name = "blob",
    boundingBox = BoundingBox.Circle(point = Point2D(100, 100), radius = 10),
    life = INIT_BLOB_LIFE,
    velocity = INIT_BLOB_VEL,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    gender = GenderValue.Male,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))

  val mud: BaseObstacle = BaseObstacle(
    name = "mud",
    boundingBox = BoundingBox.Rectangle(point = Point2D(100, 100), width = 50, height = 40),
    effect = Effect.slowEffect)

  val stone: BaseObstacle = BaseObstacle(
    name = "stone",
    boundingBox = BoundingBox.Rectangle(point = Point2D(100, 100), width = 50, height = 40),
    effect = Effect.damageEffect)

  test("BlobSlowed") {
    val updatedBlob = blob.collided(mud).toVector(0).asInstanceOf[Blob]
    assert(updatedBlob.velocity == (INIT_BLOB_VEL - Constants.VELOCITY_SLOW_DECREMENT))
  }

  test("BlobDamaged") {
    val updatedBlob = blob.collided(stone).toVector(0).asInstanceOf[Blob]
    assert(updatedBlob.life == (INIT_BLOB_LIFE - Constants.DEF_DAMAGE))
  }

  test("BlobKilled") {
    val updatedBlob = chain(4)(blob)(b =>
      b.collided(stone).toVector(0).asInstanceOf[BaseBlob])
    assert(updatedBlob.life == 0)
  }

}