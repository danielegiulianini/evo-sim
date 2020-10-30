import evo_sim.model.Entities.StandardPlant
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.{BoundingBox, World}
import evo_sim.model.Constants._
import org.scalatest.FunSuite

class PlantTests extends FunSuite {
  private val plant = StandardPlant(
    name = "standardPlant1",
    boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = DEF_STANDARD_PLANT_WIDTH, height = DEF_STANDARD_PLANT_HEIGHT),
    lifeCycle = DEF_LIFECYCLE)
  private val plant2 = StandardPlant(
    name = "standardPlant2",
    boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = DEF_STANDARD_PLANT_WIDTH, height = DEF_STANDARD_PLANT_HEIGHT),
    lifeCycle = 0)
  private val world: World = World.apply(temperature = DEF_TEMPERATURE, luminosity = DEF_LUMINOSITY, width = WORLD_WIDTH, height = WORLD_HEIGHT,
    currentIteration = 0, entities = Set(plant, plant2), totalIterations = DEF_DAYS * ITERATIONS_PER_DAY)

  test("updatedPlant") {
    val updatedPlant = plant.updated(world)
    val updatedPlant2 = plant2.updated(world)
    assert(updatedPlant.exists {
      case p: StandardPlant => p.lifeCycle == plant.lifeCycle - 1
      case _ => false
    })
    assert(updatedPlant2.exists {
      case p: StandardPlant => p.lifeCycle == DEF_LIFECYCLE
      case _ => false
    })
  }
}
