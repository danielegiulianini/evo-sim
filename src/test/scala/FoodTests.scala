import evo_sim.model.Entities.{BaseBlob, BaseFood, PoisonBlob}
import evo_sim.model.EntityStructure.Blob
import evo_sim.model._
import evo_sim.model.Constants._
import org.scalatest.FunSpec

class FoodTests extends FunSpec {

  val blob: BaseBlob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))

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
    degradationEffect = DegradationEffect.standardDegradation,
    life = 100,
    effect = Effect.standardFoodEffect)

  val reproducingFood: BaseFood = standardFood.copy(name = "food2", effect = Effect.reproduceBlobFoodEffect)

  val poisonFood: BaseFood = standardFood.copy(name = "food3", effect = Effect.poisonousFoodEffect)

  describe("A BaseBlob") {
    describe("when colliding with a food with standardFoodEffect") {
      it("should increase its energy") {
        assert(blob.collided(standardFood).exists(e => e match {
          case b : Blob => b.life == blob.life + DEF_FOOD_ENERGY
          case _ => false
        }))
      }
      it("should return a set with one blob") {
        assert(blob.collided(standardFood).size == 1)
      }
    }
    describe("when colliding with a food with reproducingFoodEffect") {
      it("should create a blob with full life") {
        assert(blob.collided(reproducingFood).exists(e => e match {
          case b : Blob => b.life == DEF_BLOB_LIFE
          case _ => false
        }))
      }
      it("should return a set with two blobs") {
        assert(blob.collided(reproducingFood).size == 2)
      }
    }
    describe("when colliding with a food with poisonousFoodEffect") {
      it("should return a PoisonBlob") {
        assert(blob.collided(poisonFood).exists(e => e match {
          case _ : PoisonBlob => true
          case _ => false
        }))
      }
      it("should return a set with one blob") {
        assert(blob.collided(poisonFood).size == 1)
      }
    }
  }

  describe("A PoisonBlob") {
    describe("when colliding with a food with standardFoodEffect") {
      it("should return a set with one blob") {
        assert(poisonBlob.collided(standardFood).size == 1)
      }
    }
    describe("when colliding with a food with reproducingFoodEffect") {
      it("should return a set with one blob") {
        assert(poisonBlob.collided(reproducingFood).size == 1)
      }
    }
  }

  describe("A Food with standardFoodEffect") {
    describe("when colliding with a BaseBlob") {
      it("should return an empty set") {
        assert(standardFood.collided(blob).size == 0)
      }
    }
  }

  describe("A Food with reproducingFoodEffect") {
    describe("when colliding with a BaseBlob") {
      it("should return an empty set") {
        assert(reproducingFood.collided(blob).size == 0)
      }
    }
  }

  describe("A Food with poisonousFoodEffect") {
    describe("when colliding with a BaseBlob") {
      it("should return an empty set") {
        assert(poisonFood.collided(blob).size == 0)
      }
    }
  }
}

