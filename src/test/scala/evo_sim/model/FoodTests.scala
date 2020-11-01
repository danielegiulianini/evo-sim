import evo_sim.model.Entities.{BaseBlob, BaseFood, CannibalBlob, PoisonBlob}
import evo_sim.model.EntityStructure.{Blob, BlobWithTemporaryStatus}
import evo_sim.model._
import evo_sim.model.Constants._
import evo_sim.model.effects.{CollisionEffect, DegradationEffect}
import evo_sim.utils.TestUtils._
import org.scalatest.FunSpec

class FoodTests extends FunSpec {
  private val blob: BaseBlob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))
  private val poisonBlob: PoisonBlob = PoisonBlob(
    name = "blob2",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15),
    cooldown = Constants.DEF_COOLDOWN)
  private val cannibalBlob: CannibalBlob = CannibalBlob(
    name = "blob3",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))
  private val standardFood: BaseFood = BaseFood(
    name = "food4",
    boundingBox = BoundingBox.Triangle.apply(point = Point2D(100, 100), height = 10),
    degradationEffect = DegradationEffect.standardDegradation,
    life = 100,
    collisionEffect = CollisionEffect.standardFoodEffect)
  private val reproducingFood: BaseFood = standardFood.copy(name = "food5", collisionEffect = CollisionEffect.reproduceBlobFoodEffect)
  private val poisonFood: BaseFood = standardFood.copy(name = "food6", collisionEffect = CollisionEffect.poisonousFoodEffect)
  private val world: World = World.apply(temperature = DEF_TEMPERATURE, luminosity = DEF_LUMINOSITY, width = WORLD_WIDTH, height = WORLD_HEIGHT,
    currentIteration = 0, entities = Set(blob, poisonBlob, cannibalBlob, standardFood, reproducingFood, poisonFood), totalIterations = DEF_DAYS * ITERATIONS_PER_DAY)

  describe("A Blob with BaseBlobBehaviour") {
    describe("when colliding with a Food with standardFoodEffect") {
      it("should increase its energy") {
        assert(blob.collided(standardFood).exists {
          case b: Blob => b.life == blob.life + DEF_FOOD_ENERGY
          case _ => false
        })
      }
      it("should return a set with one Blob") {
        assert(blob.collided(standardFood).size == 1)
      }
    }
    describe("when colliding with a Food with reproducingFoodEffect") {
      it("should create a Blob with full life") {
        assert(blob.collided(reproducingFood).exists {
          case b: Blob => b.life == DEF_BLOB_LIFE
          case _ => false
        })
      }
      it("should return a set with two Blobs") {
        assert(blob.collided(reproducingFood).size == 2)
      }
    }
    describe("when colliding with a food with poisonousFoodEffect") {
      it("should return a PoisonBlob") {
        assert(blob.collided(poisonFood).exists {
          case _: PoisonBlob => true
          case _ => false
        })
      }
      it("should return a set with one Blob") {
        assert(blob.collided(poisonFood).size == 1)
      }
    }
  }

  describe("A Blob with CannibalBlobBehaviour") {
    describe("when colliding with a Food with standardFoodEffect") {
      it("should increase its energy") {
        assert(cannibalBlob.collided(standardFood).exists {
          case b: Blob => b.life == cannibalBlob.life + DEF_FOOD_ENERGY
          case _ => false
        })
      }
      it("should return a set with one Blob") {
        assert(cannibalBlob.collided(standardFood).size == 1)
      }
    }
    describe("when colliding with a Food with reproducingFoodEffect") {
      it("should create a Blob with full life") {
        assert(cannibalBlob.collided(reproducingFood).exists {
          case b: Blob => b.life == DEF_BLOB_LIFE
          case _ => false
        })
      }
      it("should return a set with two Blobs") {
        assert(cannibalBlob.collided(reproducingFood).size == 2)
      }
    }
    describe("when colliding with a food with poisonousFoodEffect") {
      it("should return a PoisonBlob") {
        assert(cannibalBlob.collided(poisonFood).exists {
          case _: PoisonBlob => true
          case _ => false
        })
      }
      it("should return a set with one Blob") {
        assert(cannibalBlob.collided(poisonFood).size == 1)
      }
    }
  }

  describe("A Blob with TemporaryStatusBlobBehaviour") {
    describe("when colliding with a Food with standardFoodEffect") {
      it("should return a set with itself") {
        assert(poisonBlob.collided(standardFood).equals(Set(poisonBlob)))
      }
    }
    describe("when colliding with a Food with reproducingFoodEffect") {
      it("should return a set with itself") {
        assert(poisonBlob.collided(reproducingFood).equals(Set(poisonBlob)))
      }
    }
    describe("when colliding with a Food with poisonousFoodEffect") {
      it("should return a set with itself") {
        assert(poisonBlob.collided(poisonFood).equals(Set(poisonBlob)))
      }
    }
  }

  describe("A Food") {
    describe("when updating") {
      it("should return a set with itself updated") {
        val updatedFood = standardFood.updated(world)
        assert(updatedFood.equals(Set(standardFood.copy(life = standardFood.degradationEffect(standardFood)))))
      }
      it("should return an empty set if life <= 0") {
        val updatedFood = standardFood.copy(life = 0).updated(world)
        assert(updatedFood.isEmpty)
      }
    }
    describe("when colliding with a Blob") {
      it("should return an empty set") {
        assert(standardFood.collided(blob).isEmpty)
      }
    }
  }
}

