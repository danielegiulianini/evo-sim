import evo_sim.model.Entities.{BaseBlob, StandardPlant}
import evo_sim.model.EntityStructure.{Food, Plant}
import evo_sim.model.{BoundingBox, Constants, Direction, MovingStrategies, Point2D, World}
import evo_sim.model.Constants._
import evo_sim.model.Point2D.randomPosition
import org.scalatest.FunSpec

class PlantTests extends FunSpec {
  private val plant = StandardPlant(
    name = "standardPlant1",
    boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = DEF_STANDARD_PLANT_WIDTH, height = DEF_STANDARD_PLANT_HEIGHT),
    lifeCycle = DEF_LIFECYCLE)
  private val plant2 = StandardPlant(
    name = "standardPlant2",
    boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = DEF_STANDARD_PLANT_WIDTH, height = DEF_STANDARD_PLANT_HEIGHT),
    lifeCycle = 0)
  private val world: World = World.apply(temperature = DEF_TEMPERATURE, luminosity = DEF_LUMINOSITY, width = WORLD_WIDTH, height = WORLD_HEIGHT,
    currentIteration = 0, entities = Set(plant, plant2), totalIterations = DEF_DAYS * ITERATIONS_PER_DAY)

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
  }
}
