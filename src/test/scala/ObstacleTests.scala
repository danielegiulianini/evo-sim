import evo_sim.model.Entities.{BaseBlob, BaseObstacle}
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.Utils.chain
import evo_sim.model._
import org.scalatest.FunSpec

class ObstacleTests extends FunSpec {
  private val blob: BaseBlob = BaseBlob(
    name = "blob",
    boundingBox = BoundingBox.Circle(point = Point2D(100, 100), radius = 10),
    life = Constants.DEF_BLOB_LIFE,
    velocity = Constants.DEF_BLOB_VELOCITY,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))
  private val puddle: BaseObstacle = BaseObstacle(
    name = "mud",
    boundingBox = BoundingBox.Rectangle(point = Point2D(100, 100), width = 50, height = 40),
    effect = Effect.slowEffect)
  private val stone: BaseObstacle = BaseObstacle(
    name = "stone",
    boundingBox = BoundingBox.Rectangle(point = Point2D(100, 100), width = 50, height = 40),
    effect = Effect.damageEffect)
  private val world: World = new World(
    temperature = Constants.DEF_TEMPERATURE,
    luminosity = Constants.DEF_LUMINOSITY,
    width = Constants.WORLD_WIDTH,
    height = Constants.WORLD_HEIGHT,
    currentIteration = 0,
    entities = Set(blob) ++ Set(puddle) ++ Set(stone),
    totalIterations = 10
  )

  describe("A BaseObstacle") {
    describe("with mud effect") {
      describe("after an update") {
        it("should never change") {
          assert(Set(puddle) == puddle.updated(world))
        }
      }
      describe("after a collision") {
        it("should never change") {
          assert(Set(puddle) == puddle.collided(blob))
        }
      }
    }
    describe("with damage effect") {
      describe("after an update") {
        it("should never change") {
          assert(Set(stone) == stone.updated(world))
        }
      }
      describe("after a collision") {
        it("should never change") {
          assert(Set(stone) == stone.collided(blob))
        }
      }
    }
  }

  describe("A BaseBlob") {
    describe("when colliding with a puddle") {
      it("should decrease its velocity") {
        val updatedBlob = blob.collided(puddle).toVector(0).asInstanceOf[Blob]
        assert(updatedBlob.velocity == 50)
      }
    }
    describe("when colliding with a stone") {
      it("should decrease its energy") {
        val updatedBlob = blob.collided(stone).toVector(0).asInstanceOf[Blob]
        assert(updatedBlob.life == (Constants.DEF_BLOB_LIFE - Constants.DEF_DAMAGE))
      }
      describe("multiple times") {
        it("should die") {
          val updatedBlob = chain(40)(blob)(b =>
            b.collided(stone).toVector(0).asInstanceOf[BaseBlob])
          assert(updatedBlob.life <= 0)
        }
      }
    }
  }
}

