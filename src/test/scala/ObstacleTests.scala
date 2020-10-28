import evo_sim.model.Entities.{BaseBlob, BaseObstacle}
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.Utils.chain
import evo_sim.model._
import org.scalatest.FunSuite

class ObstacleTests extends FunSuite {

  val blob: BaseBlob = BaseBlob(
    name = "blob",
    boundingBox = BoundingBox.Circle(point = Point2D(100, 100), radius = 10),
    life = Constants.DEF_BLOB_LIFE,
    velocity = Constants.DEF_BLOB_VELOCITY,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
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
    assert(updatedBlob.velocity == 50)
  }

  test("BlobDamaged") {
    val updatedBlob = blob.collided(stone).toVector(0).asInstanceOf[Blob]
    assert(updatedBlob.life == (Constants.DEF_BLOB_LIFE - Constants.DEF_DAMAGE))
  }

  test("BlobKilled") {
    val updatedBlob = chain(40)(blob)(b =>
      b.collided(stone).toVector(0).asInstanceOf[BaseBlob])
    assert(updatedBlob.life == 0)
  }

}

