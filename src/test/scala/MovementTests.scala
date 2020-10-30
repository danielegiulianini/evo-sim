import evo_sim.model.Entities.{BaseBlob, BaseFood}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model._
import org.scalatest.FunSuite

class MovementTests extends FunSuite {

  /*val blob: BaseBlob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle.apply(point = Point2D(100, 100), radius = 10),
    life = 100,
    velocity = 3,
    degradationEffect = DegradationEffect.standardDegradation,
    fieldOfViewRadius = 10,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction(0, 15))*/

  val blob = BaseBlob(
    name = "blob1",
    boundingBox = BoundingBox.Circle(Point2D(100, 100), radius = Constants.DEF_BLOB_RADIUS),
    life = Constants.DEF_BLOB_LIFE,
    velocity = Constants.DEF_BLOB_VELOCITY,
    degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
    fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
    movementStrategy = MovingStrategies.baseMovement,
    direction = Direction.apply(Constants.DEF_NEXT_DIRECTION, Constants.DEF_NEXT_DIRECTION))

  val entities = Set[SimulableEntity](blob)

  val world: World = World(
    temperature = Constants.DEF_TEMPERATURE,
    luminosity = Constants.DEF_LUMINOSITY,
    width = Constants.WORLD_WIDTH,
    height = Constants.WORLD_HEIGHT,
    currentIteration = 0,
    entities = entities,
    totalIterations = Constants.DEF_DAYS * Constants.ITERATIONS_PER_DAY)

  test("RandomMovement"){
    val initialPosition = blob.boundingBox.point
    val newPosition = MovingStrategies.baseMovement(blob, world, _.isInstanceOf[BaseFood]).point
    assert(!initialPosition.equals(newPosition))
  }

}
