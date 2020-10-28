import evo_sim.model.Entities.{BaseBlob, PoisonBlob}
import evo_sim.model.World.randomPosition
import evo_sim.model._
import org.scalatest.FunSuite

class DegradationTests extends FunSuite {
  private val LIFE_AFTER_STANDARD_EFFECT = 998
  private val LIFE_AFTER_POISON_EFFECT = 994

  val blob = PoisonBlob(
    name = "blob",
    boundingBox = BoundingBox.Circle(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
    life = Constants.DEF_BLOB_LIFE,
    velocity = Constants.DEF_BLOB_VELOCITY,
    degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
    fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction.apply(Constants.DEF_NEXT_DIRECTION, Constants.DEF_NEXT_DIRECTION),
    cooldown = Constants.DEF_COOLDOWN)

  test("Standard Degradation effect") {
    val newBlob = blob.copy(life = DegradationEffect.standardDegradation(blob))
    assert(newBlob.life==LIFE_AFTER_STANDARD_EFFECT)
  }

  test("Poison Degradation effect") {
    val newBlob = blob.copy(life = DegradationEffect.poisonBlobDegradation(blob))
    assert(newBlob.life==LIFE_AFTER_POISON_EFFECT)
  }
}