package evo_sim.model

import evo_sim.dsl.EntitiesCreation.FromIntToList
import evo_sim.model.Constants.ITERATIONS_PER_DAY
import evo_sim.model.Entities._
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.utils.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidalSin
import evo_sim.model.World.MemoHelper.memoize
import evo_sim.model.World.WorldHistory

/** Represents the state of the simulation and acts as a container for all of its properties that are carried along
 * each iteration.
 * @param temperature the actual temperature at the current iteration.
 * @param luminosity the actual luminosity at the current iteration.
 * @param width the width of simulation area.
 * @param height the height of simulation area.
 * @param currentIteration the point in time to which this World refers to.
 * @param entities the set of entities actually present inside the simulation area.
 * @param totalIterations the total iterations count before the end of the simulation.
 * @param worldHistory the collection of World instances (each corresponding to every single iteration) gathered since the beginning of the simulation.
 */
case class World(temperature: Int,
                 luminosity: Int,
                 width: Int,
                 height: Int,
                 currentIteration: Int,
                 entities: Set[SimulableEntity],
                 totalIterations: Int,
                 worldHistory: WorldHistory = Stream.empty)


//companion object
object World {

  type WorldHistory = Stream[World]

  def randomPosition(): Point2D = Point2D.apply(new scala.util.Random().nextInt(Constants.WORLD_WIDTH.+(1)),
    new scala.util.Random().nextInt(Constants.WORLD_HEIGHT.+(1)))

  def apply(env: Environment): World = {

    val baseBlobs: Set[BaseBlob] = (env.initialBlobNumber.toDouble / 2).ceil.toInt of BaseBlob(
      name = "blob" + Utils.nextValue(),
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction.apply(0, Constants.DEF_NEXT_DIRECTION))

    val cannibalBlobs: Set[CannibalBlob] = env.initialBlobNumber.toDouble./(2).floor.toInt of CannibalBlob(
      name = "cannibalBlob" + Utils.nextValue(),
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = 2 * Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction(0, Constants.DEF_NEXT_DIRECTION))

    val stones: Set[BaseObstacle] = env.initialObstacleNumber.toDouble./(2).ceil.toInt of BaseObstacle(
      name = "stone"+Utils.nextValue(),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_STONE_WIDTH, height = Constants.DEF_STONE_HEIGHT),
      effect = Effect.damageEffect)

    val puddles: Set[BaseObstacle] =(env.initialObstacleNumber.toDouble./(2).floor.toInt) of BaseObstacle(
      name = "puddle"+Utils.nextValue(),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_PUDDLE_WIDTH, height = Constants.DEF_PUDDLE_HEIGHT),
      effect = Effect.slowEffect)

    val standardPlants: Set[StandardPlant] = (env.initialPlantNumber.toDouble / 2).floor.toInt of StandardPlant(
      name = "standardPlant"+Utils.nextValue(),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_STANDARD_PLANT_WIDTH, height = Constants.DEF_STANDARD_PLANT_HEIGHT),
      lifeCycle = 0)

    val reproducingPlants: Set[ReproducingPlant] = (env.initialPlantNumber.toDouble / 4).ceil.toInt of ReproducingPlant(
      name = "reproducingPlant"+Utils.nextValue(),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_REPRODUCING_PLANT_WIDTH * 3 / 2, height = Constants.DEF_REPRODUCING_PLANT_WIDTH),
      lifeCycle = 0)

    val poisonousPlants: Set[PoisonousPlant] = (env.initialPlantNumber.toDouble / 4).ceil.toInt of PoisonousPlant(
      name = "poisonousPlant"+Utils.nextValue(),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_POISONOUS_PLANT_WIDTH * 3 / 2, height = Constants.DEF_POISONOUS_PLANT_WIDTH),
      lifeCycle = 0)

    val entities: Set[SimulableEntity] = baseBlobs ++ cannibalBlobs ++ stones ++ puddles ++ standardPlants ++ reproducingPlants ++ poisonousPlants

    World(temperature = env.temperature, luminosity = env.luminosity, width = Constants.WORLD_WIDTH, height = Constants.WORLD_HEIGHT,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * Constants.ITERATIONS_PER_DAY)
  }


  /** The EnvironmentParameters class holds the set of parameters of the world that can be updated.
   *
   * @param luminosity  a luminosity value
   * @param temperature a temperature value
   */
  case class EnvironmentParameters(luminosity: Int, temperature: Int)

  /** Updates the world parameters according to the time of the day of the simulation.
   *
   * @param world the world with the parameters to update
   * @return the [[evo_sim.model.World.EnvironmentParameters]] container with updated parameters
   */
  def worldEnvironmentUpdated(world: World): EnvironmentParameters = {

    //leveraging Flyweight pattern for sin computation (sin is: 1. cyclic, periodic and 2. computationally-expensive
    // (wrt integer arithmetics) function so caching is effective as the values would be recomputed
    // every time, unnecessarily)
    val luminosityUpdated: ((Int, Float)) => Int = memoize({
      case (luminosity, timeOfTheDay) =>
        luminosity + zeroPhasedZeroYTranslatedSinusoidalSin(Constants.LUMINOSITY_AMPLITUDE)(timeOfTheDay)
    })

    val temperatureUpdated: ((Int, Float)) => Int = memoize({
      case (temperature, timeOfTheDay) =>
        temperature + zeroPhasedZeroYTranslatedSinusoidalSin(Constants.TEMPERATURE_AMPLITUDE)(timeOfTheDay)
    })

    val time = timeOfTheDay(world.currentIteration)
    EnvironmentParameters(
      luminosityUpdated(world.luminosity, time),
      temperatureUpdated(world.temperature, time))
  }


  object MemoHelper {
    def memoize[I, O](f: I => O): I => O = new collection.mutable.HashMap[I, O]() {
      override def apply(key: I): O = getOrElseUpdate(key, f(key))
    }
  }

  def fromIterationsToDays(iteration : Int) = iteration / ITERATIONS_PER_DAY
  def fromDaysToIterations(days : Int) = days * ITERATIONS_PER_DAY


  /** Returns the time of the day as a float ranging from 0 to 1 excluded given the iteration number.
   * 0 is midnight, 0.5 is noon and so on.
   *
   * @param iteration iteration number
   * @return time of the day
   */
  def timeOfTheDay(iteration: Int): Float =
    iteration % Constants.ITERATIONS_PER_DAY / Constants.ITERATIONS_PER_DAY.toFloat
}

/*
def apply(env: Environment): World = {

    val baseBlobs: Set[BaseBlob] = Iterator.tabulate((env.initialBlobNumber.toDouble / 2).ceil.toInt)(i => BaseBlob(
      name = "blob" + i,
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction.apply(Constants.DEF_NEXT_DIRECTION, Constants.DEF_NEXT_DIRECTION))).toSet

    val cannibalBlobs: Set[CannibalBlob] = Iterator.tabulate(env.initialBlobNumber.toDouble./(2).floor.toInt)(i => CannibalBlob(
      name = "cannibalBlob" + i,
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = 2 * Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction(Constants.DEF_NEXT_DIRECTION, Constants.DEF_NEXT_DIRECTION))).toSet

    val stones: Set[BaseObstacle] = Iterator.tabulate(env.initialObstacleNumber.toDouble./(2).ceil.toInt)((i: Int) => BaseObstacle.apply(
      name = "stone".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_STONE_WIDTH, height = Constants.DEF_STONE_HEIGHT),
      effect = Effect.damageEffect)).toSet

    val puddles: Set[BaseObstacle] = Iterator.tabulate(env.initialObstacleNumber.toDouble./(2).floor.toInt)((i: Int) => BaseObstacle.apply(
      name = "puddle".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_PUDDLE_WIDTH, height = Constants.DEF_PUDDLE_HEIGHT),
      effect = Effect.slowEffect)).toSet

    val standardPlants: Set[StandardPlant] = Iterator.tabulate((env.initialPlantNumber.toDouble / 2).floor.toInt)((i: Int) => StandardPlant(
      name = "standardPlant".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_STANDARD_PLANT_WIDTH, height = Constants.DEF_STANDARD_PLANT_HEIGHT),
      lifeCycle = 0)).toSet

    val reproducingPlants: Set[ReproducingPlant] = Iterator.tabulate((env.initialPlantNumber.toDouble / 4).ceil.toInt)((i: Int) => ReproducingPlant(
      name = "reproducingPlant".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_REPRODUCING_PLANT_WIDTH * 3 / 2, height = Constants.DEF_REPRODUCING_PLANT_WIDTH),
      lifeCycle = 0)).toSet

    val poisonousPlants: Set[PoisonousPlant] = Iterator.tabulate((env.initialPlantNumber.toDouble / 4).ceil.toInt)((i: Int) => PoisonousPlant(
      name = "poisonousPlant".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_POISONOUS_PLANT_WIDTH * 3 / 2, height = Constants.DEF_POISONOUS_PLANT_WIDTH),
      lifeCycle = 0)).toSet

    val entities: Set[SimulableEntity] = baseBlobs ++ cannibalBlobs ++ stones ++ puddles ++ standardPlants ++ reproducingPlants ++ poisonousPlants

    World(temperature = env.temperature, luminosity = env.luminosity, width = Constants.WORLD_WIDTH, height = Constants.WORLD_HEIGHT,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * Constants.ITERATIONS_PER_DAY)
  }

 */