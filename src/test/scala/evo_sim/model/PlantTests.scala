import evo_sim.model.entities.Entities.{BaseBlob, StandardPlant}
import evo_sim.model.entities.entityStructure.EntityStructure.{Food, Plant}
import evo_sim.model.world.Constants._
import evo_sim.model.entities.entityStructure.Point2D.randomPosition
import evo_sim.model.entities.entityStructure.effects.DegradationEffect
import evo_sim.model.entities.entityStructure.{BoundingBox, Point2D}
import evo_sim.model.entities.entityStructure.movement.{Direction, MovingStrategies}
import evo_sim.model.world.World
import org.scalatest.FunSpec

class PlantTests extends FunSpec {
  private val blob: BaseBlob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))
  private val plant = StandardPlant(
    name = "standardPlant2",
    boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = DEF_STANDARD_PLANT_WIDTH, height = DEF_STANDARD_PLANT_HEIGHT),
    lifeCycle = DEF_LIFECYCLE)
  private val plant2 = StandardPlant(
    name = "standardPlant3",
    boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = DEF_STANDARD_PLANT_WIDTH, height = DEF_STANDARD_PLANT_HEIGHT),
    lifeCycle = 0)
  private val world: World = World(temperature = DEF_TEMPERATURE, luminosity = DEF_LUMINOSITY, width = WORLD_WIDTH, height = WORLD_HEIGHT,
    currentIteration = 0, entities = Set(blob, plant, plant2), totalIterations = DEF_DAYS * ITERATIONS_PER_DAY)

  describe("A Plant with full lifeCycle") {
    describe("when updating") {
      val updatedPlant = plant.updated(world)
      it("should decrease its lifeCycle") {
        assert(updatedPlant.exists {
          case p: Plant => p.lifeCycle == plant.lifeCycle - 1
          case _ => false
        })
      }
    }
    describe("when colliding with a Blob") {
      val collidedPlant = plant.collided(blob)
      it("should return a set with itself") {
        assert(collidedPlant.equals(Set(plant)))
      }
    }
  }

  describe("A Plant with lifeCycle equals to 0") {
    describe("when updating") {
      val updatedPlant = plant2.updated(world)
      it("should set its lifeCycle to default value") {
        assert(updatedPlant.exists {
          case p: Plant => p.lifeCycle == DEF_LIFECYCLE
          case _ => false
        })
      }
      it("should return a Food") {
        assert(updatedPlant.exists {
          case _: Food => true
          case _ => false
        })
      }
    }
    describe("when colliding with a Blob") {
      val collidedPlant = plant.collided(blob)
      it("should return a set with itself") {
        assert(collidedPlant.equals(Set(plant)))
      }
    }
  }
}
