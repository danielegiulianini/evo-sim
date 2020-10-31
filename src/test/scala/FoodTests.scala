import evo_sim.model.Entities.{BaseBlob, BaseFood, PoisonBlob}
import evo_sim.model.EntityStructure.{Blob, BlobWithTemporaryStatus}
import evo_sim.model._
import evo_sim.model.Constants._
import evo_sim.model.effects.{DegradationEffect, Effect}
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
  private val standardFood: BaseFood = BaseFood(
    name = "food3",
    boundingBox = BoundingBox.Triangle.apply(point = Point2D(100, 100), height = 10),
    degradationEffect = DegradationEffect.standardDegradation,
    life = 100,
    effect = Effect.standardFoodEffect)
  private val reproducingFood: BaseFood = standardFood.copy(name = "food4", effect = Effect.reproduceBlobFoodEffect)
  private val poisonFood: BaseFood = standardFood.copy(name = "food5", effect = Effect.poisonousFoodEffect)
  private val world: World = World.apply(temperature = DEF_TEMPERATURE, luminosity = DEF_LUMINOSITY, width = WORLD_WIDTH, height = WORLD_HEIGHT,
    currentIteration = 0, entities = Set(blob, poisonBlob, standardFood, reproducingFood, poisonFood), totalIterations = DEF_DAYS * ITERATIONS_PER_DAY)

  describe("A BaseBlob with BaseBlobBehaviour") {
    describe("when updating with life greater than 0") {
      it("should apply its degradationEffect to its life") {
        val updatedBlob = blob.updated(world)
        assert(updatedBlob.exists {
          case b: Blob => b.life == blob.degradationEffect(blob)
          case _ => false
        })
      }
    }
    describe("when updating with life equals or less than 0") {
      val blobWithZeroLife = blob.copy(life = 0)
      it("should decrease its energy") {
        val updatedBlob = blobWithZeroLife.updated(world)
        assert(updatedBlob.isEmpty)
      }
    }
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

  describe("A PoisonBlob with PoisonBlobBehaviour") {
    describe("when updating with life greater than 0") {
      val updatedBlob = poisonBlob.updated(world)
      it("should apply its degradationEffect to its life") {
        assert(updatedBlob.exists {
          case b: Blob => b.life == DegradationEffect.poisonBlobDegradation(poisonBlob)
          case _ => false
        })
      }
      it("should decrease its cooldown") {
        assert(updatedBlob.exists {
          case b: BlobWithTemporaryStatus => b.cooldown == poisonBlob.cooldown - 1
          case _ => false
        })
      }
      it("should return a BaseBlob if cooldown reaches 0") {
        val updatedBlobWithZeroCooldown = poisonBlob.copy(cooldown = 0).updated(world)
        assert(updatedBlobWithZeroCooldown.exists {
          case _: BaseBlob => true
          case _ => false
        })
      }
    }
    describe("when updating with life equals or less than 0") {
      val blobWithZeroLife = poisonBlob.copy(life = 0)
      it("should decrease its energy") {
        val updatedBlob = blobWithZeroLife.updated(world)
        assert(updatedBlob.isEmpty)
      }
    }
    describe("when colliding with a Food with standardFoodEffect") {
      it("should return a set with one Blob") {
        assert(poisonBlob.collided(standardFood).size == 1)
      }
    }
    describe("when colliding with a Food with reproducingFoodEffect") {
      it("should return a set with one Blob") {
        assert(poisonBlob.collided(reproducingFood).size == 1)
      }
    }
  }

  describe("A Food with standardFoodEffect") {
    describe("when colliding with a BaseBlob") {
      it("should return an empty set") {
        assert(standardFood.collided(blob).isEmpty)
      }
    }
  }

  describe("A Food with reproducingFoodEffect") {
    describe("when colliding with a BaseBlob") {
      it("should return an empty set") {
        assert(reproducingFood.collided(blob).isEmpty)
      }
    }
  }

  describe("A Food with poisonousFoodEffect") {
    describe("when colliding with a BaseBlob") {
      it("should return an empty set") {
        assert(poisonFood.collided(blob).isEmpty)
      }
    }
  }
}

