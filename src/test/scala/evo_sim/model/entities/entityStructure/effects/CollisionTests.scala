package evo_sim.model.entities.entityStructure.effects

import evo_sim.model.entities.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.entities.entityStructure.EntityStructure.Blob
import evo_sim.model.entities.entityStructure.{BoundingBox, Point2D}
import evo_sim.model.entities.entityStructure.movement.{Direction, MovingStrategies}
import evo_sim.model.world.Constants
import org.scalatest.FunSpec

class CollisionTests extends FunSpec {

  private val base: BaseBlob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))
  private val cannibal: CannibalBlob = CannibalBlob(
    name = "blob2",
    boundingBox = BoundingBox.Circle(point = Point2D(100, 100), radius = 100),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))

  describe("Standard food effect") {
    it("should increase BaseBlob life"){
      assert(CollisionEffect.standardFoodEffect(base).equals(Set(base.copy(life = base.life + Constants.DEF_FOOD_ENERGY))))
    }
    it("should increase CannibalBlob life"){
      assert(CollisionEffect.standardFoodEffect(cannibal).equals(Set(cannibal.copy(life = cannibal.life + Constants.DEF_FOOD_ENERGY))))
    }
  }

  describe("Reproducing food effect") {
    it("should increase BaseBlob life"){
      assert(CollisionEffect.reproduceBlobFoodEffect(base).exists(Set(base.copy(life = base.life + Constants.DEF_FOOD_ENERGY))))
    }
    it("should create BaseBlob child"){
      assert(CollisionEffect.reproduceBlobFoodEffect(base).filter({
        case _: Blob => true
        case _ => false
      }).size == 2)
    }
    it("should increase CannibalBlob life"){
      assert(CollisionEffect.reproduceBlobFoodEffect(cannibal).exists(Set(cannibal.copy(life = cannibal.life + Constants.DEF_FOOD_ENERGY))))
    }
    it("should create CannibalBlob child"){
      assert(CollisionEffect.reproduceBlobFoodEffect(base).filter({
        case _: Blob => true
        case _ => false
      }).size == 2)
    }
  }

  describe("Poisonous food effect") {
    it("should convert BaseBlob to PoisonBlob"){
      assert(CollisionEffect.poisonousFoodEffect(base).exists({
        case b: PoisonBlob => b.name.equals(base.name)
        case _ => false
      }))
    }
    it("should convert CannibalBlob to PoisonBlob"){
      assert(CollisionEffect.poisonousFoodEffect(cannibal).exists({
        case b: PoisonBlob => b.name.equals(cannibal.name)
        case _ => false
      }))
    }
  }

  describe("Neutral effect") {
    it("should return BaseBlob unchanged"){
      assert(CollisionEffect.neutralEffect(base).equals(Set(base)))
    }
    it("should return CannibalBlob unchanged"){
      assert(CollisionEffect.neutralEffect(cannibal).equals(Set(cannibal)))
    }
  }

  describe("Slow effect") {
    it("should return BaseBlob unchanged"){
      assert(CollisionEffect.slowEffect(base).exists({
        case b: SlowBlob => b.name.equals(base.name)
        case _ => false
      }))
    }
    it("should return CannibalBlob unchanged"){
      assert(CollisionEffect.slowEffect(cannibal).exists({
        case b: SlowBlob => b.name.equals(cannibal.name)
        case _ => false
      }))
    }
  }

  describe("Damage food effect") {
    it("should decrease BaseBlob life"){
      assert(CollisionEffect.damageEffect(base).equals(Set(base.copy(life = base.life - Constants.DEF_DAMAGE))))
    }
    it("should decrease CannibalBlob life"){
      assert(CollisionEffect.damageEffect(cannibal).equals(Set(cannibal.copy(life = cannibal.life - Constants.DEF_DAMAGE))))
    }
  }

}