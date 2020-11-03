package evo_sim.model.world

import evo_sim.dsl.EntitiesCreation.FromIntToList
import Constants.ITERATIONS_PER_DAY
import evo_sim.model.entities.Entities._
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.Point2D.randomPosition
import evo_sim.model.entities.entityStructure.effects.{CollisionEffect, DegradationEffect}
import evo_sim.model.entities.entityStructure.movement.{Direction, MovingStrategies}
import evo_sim.model.entities.entityStructure.{BoundingBox, EntityStructure}
import evo_sim.model.world.World.WorldHistory
import evo_sim.model.world
import evo_sim.utils.Counter._
import evo_sim.utils.MemoHelper.memoize
import evo_sim.utils.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidal

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


/** A factory companion object for World with some utilities to handle simulation time (days and iterations)
 * conversions and day-cycle managemenent.
 */
object World {

  /** Data structure that will hold historical data used for statistical purpose at the end of simulation.
   * Stream's lazy evaluation allow to gather this data without actually impacting on real-time simulation
   * performances.
   * Saving such an amount of data using an eager collection like a list would be impractical because of the
   * expensive creation of intermediate collections rapidly growing in size.
   * */
  type WorldHistory = Stream[World]

  /** Creates a World from an [[Environment]] instance.
   * @param env the environment from which the World has to be created.
   * @return a World instance corresponding to the Environment provided
   */
  def apply(env: Environment): World = {

    val baseBlobs: Set[BaseBlob] = (env.initialBlobNumber.toDouble / 2).ceil.toInt of BaseBlob(
      name = "blob" + nextValue(),
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
      fieldOfViewRadius = Constants.DEF_BLOB_FOV_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction(0, Constants.DEF_NEXT_DIRECTION))

    val cannibalBlobs: Set[CannibalBlob] = env.initialBlobNumber.toDouble./(2).floor.toInt of CannibalBlob(
      name = "cannibalBlob" + nextValue(),
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = 2 * Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOV_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction(0, Constants.DEF_NEXT_DIRECTION))

    val stones: Set[BaseObstacle] = env.initialObstacleNumber.toDouble./(2).ceil.toInt of BaseObstacle(
      name = "stone" + nextValue(),
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_STONE_WIDTH, height = Constants.DEF_STONE_HEIGHT),
      collisionEffect = CollisionEffect.damageEffect)

    val puddles: Set[BaseObstacle] = env.initialObstacleNumber.toDouble./(2).floor.toInt of BaseObstacle(
      name = "puddle" + nextValue(),
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_PUDDLE_WIDTH, height = Constants.DEF_PUDDLE_HEIGHT),
      collisionEffect = CollisionEffect.slowEffect)

    val standardPlants: Set[StandardPlant] = (env.initialPlantNumber.toDouble / 2).floor.toInt of StandardPlant(
      name = "standardPlant" + nextValue(),
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_STANDARD_PLANT_WIDTH, height = Constants.DEF_STANDARD_PLANT_HEIGHT),
      lifeCycle = 0)

    val reproducingPlants: Set[ReproducingPlant] = (env.initialPlantNumber.toDouble / 4).ceil.toInt of ReproducingPlant(
      name = "reproducingPlant" + nextValue(),
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_REPRODUCING_PLANT_WIDTH * 3 / 2, height = Constants.DEF_REPRODUCING_PLANT_WIDTH),
      lifeCycle = 0)

    val poisonousPlants: Set[PoisonousPlant] = (env.initialPlantNumber.toDouble / 4).ceil.toInt of PoisonousPlant(
      name = "poisonousPlant" + nextValue(),
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_POISONOUS_PLANT_WIDTH * 3 / 2, height = Constants.DEF_POISONOUS_PLANT_WIDTH),
      lifeCycle = 0)

    val entities: Set[SimulableEntity] = baseBlobs ++ cannibalBlobs ++ stones ++ puddles ++ standardPlants ++ reproducingPlants ++ poisonousPlants

    world.World(temperature = env.temperature, luminosity = env.luminosity, width = Constants.WORLD_WIDTH, height = Constants.WORLD_HEIGHT,
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
   * @return the [[world.World.EnvironmentParameters]] container with updated parameters
   */
  def worldEnvironmentUpdated(world: World): EnvironmentParameters = {

    //leveraging Flyweight pattern for sin computation (sin is: 1. cyclic, periodic and 2. computationally-expensive
    // (wrt integer arithmetics) function so caching is effective as the values would be recomputed
    // every time, unnecessarily)
    val luminosityUpdated: ((Int, Float)) => Int = memoize({
      case (luminosity, timeOfTheDay) =>
        luminosity + zeroPhasedZeroYTranslatedSinusoidal(Constants.LUMINOSITY_AMPLITUDE)(timeOfTheDay)
    })

    val temperatureUpdated: ((Int, Float)) => Int = memoize({
      case (temperature, timeOfTheDay) =>
        temperature + zeroPhasedZeroYTranslatedSinusoidal(Constants.TEMPERATURE_AMPLITUDE)(timeOfTheDay)
    })

    val time = timeOfTheDay(world.currentIteration)
    EnvironmentParameters(
      luminosityUpdated(world.luminosity, time),
      temperatureUpdated(world.temperature, time))
  }


  /** Converts from simulation iterations (the atomic temporal unit that regulates the
   * simulation) to days (the temporal interval between which [[EnvironmentParameters]]
   * return the same), depending on [[Constants.ITERATIONS_PER_DAY]].
   * @param iteration iteration
   * @return the days amount corresponding to the iteration
   */
  def fromIterationsToDays(iteration : Int): Int = iteration / ITERATIONS_PER_DAY

  /** Converts from simulation days (the temporal interval between which [[EnvironmentParameters]]
   * return the same) and iterations (the atomic temporal unit that regulates the
   * simulation) to iterations, depending on [[Constants.ITERATIONS_PER_DAY]].
   *
   * @param day day
   * @return the iterations amount corresponding to the days
   */
  def fromDaysToIterations(day : Int): Int = day * ITERATIONS_PER_DAY


  /** Returns the time of the day as a float ranging from 0 to 1 excluded given the iteration number.
   * 0 is midnight, 0.5 is noon and so on.
   *
   * @param iteration iteration number
   * @return time of the day
   */
  def timeOfTheDay(iteration: Int): Float =
    iteration % Constants.ITERATIONS_PER_DAY / Constants.ITERATIONS_PER_DAY.toFloat
}


