package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World.TrigonometricalOps.{sinusoidalSin, zeroPhasedOneYTranslatedSinusoidalSin, zeroPhasedSinusoidalSin}



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

  def randomPosition(): Point2D = Point2D(new scala.util.Random().nextInt(Constants.WORLD_WIDTH + 1),
    new scala.util.Random().nextInt(Constants.WORLD_HEIGHT + 1))

  def apply(env: Environment): World = {
    val blobs: Set[BaseBlob] = Iterator.tabulate(env.initialBlobNumber)(i => BaseBlob(
      name = "blob" + i,
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction(0, 20))).toSet

    val standardFoods: Set[BaseFood] = Iterator.tabulate((env.initialFoodNumber / 2).ceil.toInt)(i => BaseFood(
      name = "standardFood" + i,
      boundingBox = BoundingBox.Triangle(point = randomPosition(), height = Constants.DEF_FOOD_HEIGHT),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.standardFoodEffect)).toSet

    val reproducingFoods: Set[BaseFood] = Iterator.tabulate((env.initialFoodNumber / 2).floor.toInt)(i => BaseFood(
      name = "reproducingFood" + i,
      boundingBox = BoundingBox.Triangle(point = randomPosition(), height = Constants.DEF_REPRODUCING_FOOD_HEIGHT),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.reproduceBlobFoodEffect)).toSet

    val stones: Set[BaseObstacle] = Iterator.tabulate((env.initialObstacleNumber / 2).ceil.toInt)(i => BaseObstacle(
      name = "stone" + i,
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_STONE_WIDTH, height = Constants.DEF_STONE_HEIGHT),
      effect = Effect.damageEffect)).toSet

    val puddles: Set[BaseObstacle] = Iterator.tabulate((env.initialObstacleNumber / 2).floor.toInt)(i => BaseObstacle(
      name = "puddle" + i,
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_PUDDLE_WIDTH, height = Constants.DEF_PUDDLE_HEIGHT),
      effect = Effect.mudEffect)).toSet

    val entities: Set[SimulableEntity] = blobs ++ standardFoods ++ reproducingFoods ++ stones ++ puddles

    World(temperature = env.temperature, luminosity = env.luminosity, width = Constants.WORLD_WIDTH, height = Constants.WORLD_HEIGHT,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * Constants.ITERATIONS_PER_DAY)
  }

  case class EnvironmentParameters(luminosity: Int, temperature: Int)

  def worldEnvironmentUpdated(world: World): EnvironmentParameters = {

    val luminosityUpdated: ((Int, Int)) => Int = MemoHelper.memoize {
      case (luminosity, currentIteration) =>
        luminosity + zeroPhasedSinusoidalSin(1 + 1/32f, currentIteration / Constants.ITERATIONS_PER_DAY, 0)
    }

    val temperatureUpdated: ((Int, Int)) => Int = MemoHelper.memoize {
      case (temperature, currentIteration) =>
        temperature + zeroPhasedOneYTranslatedSinusoidalSin(1 + 1/64f, currentIteration/Constants.ITERATIONS_PER_DAY)
    }



    EnvironmentParameters(luminosityUpdated(world.luminosity, world.currentIteration),
      temperatureUpdated(world.temperature, world.currentIteration))
  }



  object MemoHelper {
    def memoize[I, O](f: I => O): I => O = new collection.mutable.HashMap[I, O]() {
      override def apply(key: I) = getOrElseUpdate(key, f(key))
    }
  }

  object TrigonometricalOps {
    def sinusoidalSin(yDilatation: Float)(x:Float)(phase: Int)(yTranslation: Int) =
      (yDilatation * Math.sin(2 * Math.PI * x + phase)).toInt + yTranslation  //should rename ytranslation to amplitude

    //most used, common and popular sinusoidalSin invocations (for this purpose translated in partially-applied functions)
    def zeroPhasedSinusoidalSin = sinusoidalSin  (_:Float) (_:Float) (0) (_:Int)
    //sinusoidalSin ((_:Float) (_:Float) (0) (_:Int) ).curried
    def zeroYTranslatedSinusoidalSin = sinusoidalSin (_:Float) (_:Float) (_:Int) (0)
    //sinusoidalSin ((_:Float) (_:Float) (_:Int) (0)).curried
    def oneYTranslatedSinusoidalSin = sinusoidalSin (_:Float) (_:Float) (_:Int) (1)
    //sinusoidalSin ((_:Float) (_:Float) (_:Int) (1) ).curried
    def zeroPhasedOneYTranslatedSinusoidalSin : (Float, Float) => Int = sinusoidalSin (_:Float) (_:Float) (0)  (1)
    //sinusoidalSin ((_:Float) (_:Float) (0) (1)).curried



    /*example of use:
    instead of calling: sinusoidalSin(1f)(2)(0)(1) in many places in our code, call:
    oneYTranslatedZeroPhasedSinusoidalSin(1f)(2)            -------->(reuse)
    */

  }

}



