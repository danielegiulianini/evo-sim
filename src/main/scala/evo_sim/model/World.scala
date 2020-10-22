package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle, BasePlant, CannibalBlob, ReproducingPlant, StandardPlant}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World.TrigonometricalOps.Sinusoidal.Curried.{zeroPhasedSinusoidalSin, zeroPhasedZeroYTranslatedSinusoidalSin}



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
      direction = Direction.apply(0, 20))).toSet

    val cannibalBlobs: Set[CannibalBlob] = Iterator.tabulate(env.initialBlobNumber / 2)(i => CannibalBlob(
      name = "cannibalBlob" + i,
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = 2*Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction(0, 20))).toSet

    val standardFoods: Set[BaseFood] = Iterator.tabulate((env.initialFoodNumber / 10 * 9).ceil.toInt)(i => BaseFood(
      name = "standardFood" + i,
      boundingBox = BoundingBox.Triangle(point = randomPosition(), height = Constants.DEF_FOOD_HEIGHT),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.standardFoodEffect)).toSet

    val reproducingFoods: Set[BaseFood] = Iterator.tabulate((env.initialFoodNumber / 10).floor.toInt)(i => BaseFood(
      name = "reproducingFood" + i,
      boundingBox = BoundingBox.Triangle(point = randomPosition(), height = Constants.DEF_REPRODUCING_FOOD_HEIGHT),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.reproduceBlobFoodEffect)).toSet

    val stones: Set[BaseObstacle] = Iterator.tabulate((env.initialObstacleNumber./(2)).ceil.toInt)((i: Int) => BaseObstacle.apply(
      name = "stone".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_STONE_WIDTH, height = Constants.DEF_STONE_HEIGHT),
      effect = Effect.damageEffect)).toSet

    val puddles: Set[BaseObstacle] = Iterator.tabulate((env.initialObstacleNumber./(2)).floor.toInt)((i: Int) => BaseObstacle.apply(
      name = "puddle".+(i),
      boundingBox = BoundingBox.Rectangle(point = World.randomPosition(), width = Constants.DEF_PUDDLE_WIDTH, height = Constants.DEF_PUDDLE_HEIGHT),
      effect = Effect.mudEffect)).toSet

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

    World.apply(temperature = env.temperature, luminosity = env.luminosity, width = Constants.WORLD_WIDTH, height = Constants.WORLD_HEIGHT,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * Constants.ITERATIONS_PER_DAY)
  }

  case class EnvironmentParameters(luminosity: Int, temperature: Int)

  def worldEnvironmentUpdated(world: World): EnvironmentParameters = {
    /*def luminosityUpdated(luminosity: Int, currentIteration: Int) =
      luminosity + ((1 + 1 / 32f) * Math.sin(2 * Math.PI * currentIteration / Constants.ITERATIONS_PER_DAY)).toInt

    def temperatureUpdated(temperature: Int, currentIteration: Int) = {
      val newTemp  = temperature + ((1 + 1 / 64f) * Math.sin(2 * Math.PI * currentIteration / Constants.ITERATIONS_PER_DAY)).toInt
      newTemp
    }*/

    val luminosityUpdated:((Int, Int)) => Int = MemoHelper.memoize({
      case (luminosity, currentIteration) =>
        luminosity + zeroPhasedSinusoidalSin(1 + 1 / 32f)(currentIteration / Constants.ITERATIONS_PER_DAY)(0)
    })

    val temperatureUpdated:((Int, Int)) => Int = MemoHelper.memoize({
      case (temperature, currentIteration) =>
        temperature + zeroPhasedZeroYTranslatedSinusoidalSin(1 + 1 / 64f)(currentIteration / Constants.ITERATIONS_PER_DAY)
    })

    EnvironmentParameters(luminosityUpdated(world.luminosity, world.currentIteration),
      temperatureUpdated(world.temperature, world.currentIteration))
  }



  object MemoHelper {
    def memoize[I, O](f: Function1[I, O]): Function1[I, O] = new collection.mutable.HashMap[I, O]() {
      override def apply(key: I): O = getOrElseUpdate(key, f(key))
    }
  }

  object TrigonometricalOps {
    object Sinusoidal {
      def sinusoidalSin(yDilatation: Float)(x:Float)(phase: Int)(yTranslation: Int): Int =
        (yDilatation * Math.sin(2 * Math.PI * x + phase)).toInt + yTranslation  //should rename yDilatation to amplitude

      //most used, common and popular sinusoidalSin invocations (for this purpose translated in partially-applied functions)
      def zeroPhasedSinusoidalSin= sinusoidalSin (_:Float) (_:Float) (0) (_:Int)
      def zeroYTranslatedSinusoidalSin = sinusoidalSin (_:Float) (_:Float) (_:Int) (0)
      def oneYTranslatedSinusoidalSin = sinusoidalSin (_:Float) (_:Float) (_:Int) (1)
      def zeroPhasedZeroYTranslatedSinusoidalSin = Curried.zeroPhasedSinusoidalSin (_:Float) (_:Float) (0)

      //object with curried versions to leverage IDE automatic named parameters
      object Curried {
        def zeroPhasedSinusoidalSin = Sinusoidal.zeroPhasedSinusoidalSin.curried
        def zeroYTranslatedSinusoidalSin = Sinusoidal.zeroYTranslatedSinusoidalSin.curried
        def oneYTranslatedSinusoidalSin = Sinusoidal.oneYTranslatedSinusoidalSin.curried
        def zeroPhasedZeroYTranslatedSinusoidalSin = Sinusoidal.zeroPhasedZeroYTranslatedSinusoidalSin.curried
      }
    }

    /*example of use:
    instead of calling: sinusoidalSin(1f)(2)(0)(1) in many places in our code, call this instead:
    zeroPhasedOneYTranslatedSinusoidalSin(1f)(2)            -------->(reuse)
    */
  }
}



