package evo_sim.model

import evo_sim.model.Constants.ITERATIONS_PER_DAY
import evo_sim.model.Entities._
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World.MemoHelper.memoize
import evo_sim.model.World.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidalSin
import evo_sim.model.Utils.timeOfTheDay
import evo_sim.model.World.WorldHistory

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

  type WorldHistory = scala.collection.immutable.Stream[World]

  def randomPosition(): Point2D = Point2D.apply(new scala.util.Random().nextInt(Constants.WORLD_WIDTH.+(1)),
    new scala.util.Random().nextInt(Constants.WORLD_HEIGHT.+(1)))

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

  /** The EnvironmentParameters class holds the set of parameters of the world that can be updated.
   *
   * @param luminosity  a luminosity value
   * @param temperature a temperature value
   */
  case class EnvironmentParameters(luminosity: Int, temperature: Int)

  /** Updates the world parameters according to the time of the dat of the simulation.
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

  object TrigonometricalOps {

    object Sinusoidal {
      def sinusoidal(yDilatation: Float)(x: Float)(phase: Int)(yTranslation: Int): Int =
        (yDilatation * Math.sin(2 * Math.PI * x + phase)).toInt + yTranslation //should rename yDilatation to amplitude

      //most used, common and popular sinusoidalSin invocations (for this purpose translated in partially-applied functions)
      def zeroPhasedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(0)(_: Int)

      def zeroYTranslatedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(_: Int)(0)

      def oneYTranslatedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(_: Int)(1)

      def zeroPhasedZeroYTranslatedSinusoidal: (Float, Float) => Int = Curried.zeroPhasedSinusoidalSin(_: Float)(_: Float)(0)

      //object with curried versions to leverage, among the others, IDE automatic named parameters
      object Curried {
        def zeroPhasedSinusoidalSin: Float => Float => Int => Int = Sinusoidal.zeroPhasedSinusoidal.curried

        def zeroYTranslatedSinusoidalSin: Float => Float => Int => Int = Sinusoidal.zeroYTranslatedSinusoidal.curried

        def oneYTranslatedSinusoidalSin: Float => Float => Int => Int = Sinusoidal.oneYTranslatedSinusoidal.curried

        def zeroPhasedZeroYTranslatedSinusoidalSin: Float => Float => Int = Sinusoidal.zeroPhasedZeroYTranslatedSinusoidal.curried
      }

    }

    /*example of use:
    instead of calling: sinusoidalSin(1f)(2)(0)(1) in many places in our code, call this instead:
    zeroPhasedOneYTranslatedSinusoidalSin(1f)(2)            -------->(reuse)
    */
  }

  def fromIterationsToDays(iteration : Int) = iteration / ITERATIONS_PER_DAY
  def fromDaysToIterations(days : Int) = days * ITERATIONS_PER_DAY

}


/*
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
 */