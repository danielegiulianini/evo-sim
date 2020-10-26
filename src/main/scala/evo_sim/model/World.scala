package evo_sim.model

import evo_sim.model.Entities._
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World.MemoHelper.memoize
import evo_sim.model.World.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidalSin


case class World(temperature: Int,
                 luminosity: Int,
                 width: Int, //to move in environment?
                 height: Int, //to move in environment?
                 currentIteration: Int,
                 entities: Set[SimulableEntity],
                 totalIterations: Int, //to move in environment?
                )

//companion object
object World {

  def randomPosition(): Point2D = Point2D.apply(new scala.util.Random().nextInt(Constants.WORLD_WIDTH.+(1)),
    new scala.util.Random().nextInt(Constants.WORLD_HEIGHT.+(1)))

  def apply(env: Environment): World = {

    val baseBlobs: Set[BaseBlob] = Iterator.tabulate(env.initialBlobNumber / 2)(i => BaseBlob(
      name = "blob" + i,
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction.apply(0, Constants.NEXT_DIRECTION))).toSet

    val cannibalBlobs: Set[CannibalBlob] = Iterator.tabulate(env.initialBlobNumber / 2)(i => CannibalBlob(
      name = "cannibalBlob" + i,
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = 2 * Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction(0, Constants.NEXT_DIRECTION))).toSet

    val standardFoods: Set[BaseFood] = Iterator.tabulate((env.initialPlantNumber / 2).ceil.toInt)(i => BaseFood(
      name = "standardFood" + i,
      boundingBox = BoundingBox.Triangle(point = randomPosition(), height = Constants.DEF_FOOD_HEIGHT),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.standardFoodEffect)).toSet

    val reproducingFoods: Set[BaseFood] = Iterator.tabulate((env.initialPlantNumber / 2).floor.toInt)(i => BaseFood(
      name = "reproducingFood" + i,
      boundingBox = BoundingBox.Triangle(point = randomPosition(), height = Constants.DEF_REPRODUCING_FOOD_HEIGHT),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.reproduceBlobFoodEffect)).toSet

    val stones: Set[BaseObstacle] = Iterator.tabulate(env.initialObstacleNumber./(2).ceil.toInt)((i: Int) => BaseObstacle.apply(
      name = "stone".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_STONE_WIDTH, height = Constants.DEF_STONE_HEIGHT),
      effect = Effect.damageEffect)).toSet

    val puddles: Set[BaseObstacle] = Iterator.tabulate(env.initialObstacleNumber./(2).floor.toInt)((i: Int) => BaseObstacle.apply(
      name = "puddle".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_PUDDLE_WIDTH, height = Constants.DEF_PUDDLE_HEIGHT),
      effect = Effect.slowEffect)).toSet

    // TODO: integrate view for plants
    val standardPlants: Set[StandardPlant] = Iterator.tabulate(5)((i: Int) => StandardPlant(
      name = "standardPlant".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_STANDARD_PLANT_WIDTH, height = Constants.DEF_STANDARD_PLANT_HEIGHT),
      lifeCycle = Constants.DEF_LIFECYCLE)).toSet

    // TODO: integrate view for plants
    val reproducingPlants: Set[ReproducingPlant] = Iterator.tabulate(5)((i: Int) => ReproducingPlant(
      name = "reproducingPlant".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_REPRODUCING_PLANT_WIDTH * 3 / 2, height = Constants.DEF_REPRODUCING_PLANT_WIDTH),
      lifeCycle = Constants.DEF_LIFECYCLE)).toSet

    val entities: Set[SimulableEntity] = baseBlobs ++ cannibalBlobs ++ standardFoods ++ reproducingFoods ++ stones ++ puddles ++ standardPlants ++ reproducingPlants

    World(temperature = env.temperature, luminosity = env.luminosity, width = Constants.WORLD_WIDTH, height = Constants.WORLD_HEIGHT,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * Constants.ITERATIONS_PER_DAY)
  }

  case class EnvironmentParameters(luminosity: Int, temperature: Int)

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

    //val timeOfTheDay = world.currentIteration / Constants.ITERATIONS_PER_DAY.toFloat
    val timeOfTheDay = world.currentIteration % Constants.ITERATIONS_PER_DAY / Constants.ITERATIONS_PER_DAY.toFloat
    EnvironmentParameters(
      luminosityUpdated(world.luminosity, timeOfTheDay),
      temperatureUpdated(world.temperature, timeOfTheDay))
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

}



