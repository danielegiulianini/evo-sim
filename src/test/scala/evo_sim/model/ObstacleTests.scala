package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseObstacle}
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.effects.{DegradationEffect, CollisionEffect}
import evo_sim.utils.TestUtils._
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
    collisionEffect = CollisionEffect.slowEffect)
  private val stone: BaseObstacle = BaseObstacle(
    name = "stone",
    boundingBox = BoundingBox.Rectangle(point = Point2D(100, 100), width = 50, height = 40),
    collisionEffect = CollisionEffect.damageEffect)
  private val world: World = new World(
    temperature = Constants.DEF_TEMPERATURE,
    luminosity = Constants.DEF_LUMINOSITY,
    width = Constants.WORLD_WIDTH,
    height = Constants.WORLD_HEIGHT,
    currentIteration = 0,
    entities = Set(blob, puddle, stone),
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
        assert(blob.collided(puddle).exists {
          case b: Blob => b.velocity == 50
          case _ => false
        })
      }
    }
    describe("when colliding with a stone") {
      it("should decrease its energy") {
        assert(blob.collided(stone).exists {
          case b: Blob => b.life == (Constants.DEF_BLOB_LIFE - Constants.DEF_DAMAGE)
          case _ => false
        })
      }
      describe("multiple times") {
        it("should die") {
          val updatedBlob = chain(Constants.DEF_BLOB_LIFE * Constants.DEF_DAMAGE)(blob)(b =>
            b.collided(stone).collect{ case b: BaseBlob => b}.head)
          assert(updatedBlob.life <= 0)
        }
      }
    }
  }
}