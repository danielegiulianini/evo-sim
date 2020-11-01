import evo_sim.model.Entities.{BaseBlob, PoisonBlob}
import evo_sim.model.Point2D.randomPosition
import evo_sim.model._
import evo_sim.model.effects.DegradationEffect
import org.scalatest.{FunSpec}

class DegradationTests extends FunSpec {
  private val LIFE_AFTER_STANDARD_EFFECT = 1248
  private val LIFE_AFTER_POISON_EFFECT = 1244

  private val blob: PoisonBlob = PoisonBlob(
    name = "blob",
    boundingBox = BoundingBox.Circle(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
    life = Constants.DEF_BLOB_LIFE,
    velocity = Constants.DEF_BLOB_VELOCITY,
    degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
    fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction.apply(Constants.DEF_NEXT_DIRECTION, Constants.DEF_NEXT_DIRECTION),
    cooldown = Constants.DEF_COOLDOWN)

  describe("Standard Degradation effect") {
    it("should apply its standard degradationEffect to its life"){
      val newBlob = blob.copy(life = DegradationEffect.standardDegradation(blob))
      assert(newBlob.life == LIFE_AFTER_STANDARD_EFFECT)
    }
  }

  describe("Poison Degradation effect") {
    it("should apply its poison degradationEffect to its life") {
      val newBlob = blob.copy(life = DegradationEffect.poisonBlobDegradation(blob))
      assert(newBlob.life == LIFE_AFTER_POISON_EFFECT)
    }
  }
}