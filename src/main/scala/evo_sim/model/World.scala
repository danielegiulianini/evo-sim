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

  def randomPosition(): Point2D = Point2D.apply(new scala.util.Random().nextInt(Constants.WORLD_WIDTH.+(1)),
    new scala.util.Random().nextInt(Constants.WORLD_HEIGHT.+(1)))

  def apply(env: Environment): World = {
    val blobs: Set[BaseBlob] = Iterator.tabulate(env.initialBlobNumber)((i: Int) => BaseBlob.apply(
      name = "blob".+(i),
      boundingBox = BoundingBox.Circle(point = World.randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = (blob: EntityStructure.Blob) => DegradationEffect.standardDegradation(blob),
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction.apply(0, 20))).toSet

    val standardFoods: Set[BaseFood] = Iterator.tabulate((env.initialFoodNumber./(2)).ceil.toInt)((i: Int) => BaseFood.apply(
      name = "standardFood".+(i),
      boundingBox = BoundingBox.Triangle(point = World.randomPosition(), height = Constants.DEF_FOOD_HEIGHT),
      degradationEffect = (food: EntityStructure.Food) => DegradationEffect.foodDegradation(food),
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.standardFoodEffect)).toSet

    val reproducingFoods: Set[BaseFood] = Iterator.tabulate((env.initialFoodNumber./(2)).floor.toInt)((i: Int) => BaseFood.apply(
      name = "reproducingFood".+(i),
      boundingBox = BoundingBox.Triangle(point = World.randomPosition(), height = Constants.DEF_REPRODUCING_FOOD_HEIGHT),
      degradationEffect = (food: EntityStructure.Food) => DegradationEffect.foodDegradation(food),
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

    val entities: Set[SimulableEntity] = (blobs ++ standardFoods ++ reproducingFoods ++ stones.++(puddles)) (Set.canBuildFrom)

    World.apply(temperature = env.temperature, luminosity = env.luminosity, width = Constants.WORLD_WIDTH, height = Constants.WORLD_HEIGHT,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * Constants.ITERATIONS_PER_DAY)
  }

  case class EnvironmentParameters(luminosity: Int, temperature: Int)

  def worldEnvironmentUpdated(world: World): EnvironmentParameters = {

    val luminosityUpdated: Function1[Tuple2[Int, Int], Int] = MemoHelper.memoize({
      case (luminosity, currentIteration) =>
        luminosity + TrigonometricalOps.zeroPhasedSinusoidalSin(1 + 1/32f, currentIteration / Constants.ITERATIONS_PER_DAY, 0)
    })

    /*def updatedLuminosity(luminosity: Int, currentIteration: Int) =
  luminosity + ((1 + 1 / 32f) * Math.sin(2 * Math.PI * currentIteration / Constants.ITERATIONS_PER_DAY)).toInt*/


    val temperatureUpdated: Function1[Tuple2[Int, Int], Int] = MemoHelper.memoize({
      case (temperature, currentIteration) =>
        temperature + TrigonometricalOps.zeroPhasedOneYTranslatedSinusoidalSin(1 + 1/64f, currentIteration/Constants.ITERATIONS_PER_DAY)
    })

    /*def updatedTemperature(tempera: Int, currentIteration: Int) =
  luminosity + ((1 + 1 / 64f) * Math.sin(2 * Math.PI * currentIteration / Constants.ITERATIONS_PER_DAY)).toInt*/


    EnvironmentParameters.apply(luminosityUpdated(world.luminosity, world.currentIteration),
      temperatureUpdated(world.temperature, world.currentIteration))
  }



  object MemoHelper {
    def memoize[I, O](f: Function1[I, O]): Function1[I, O] = new collection.mutable.HashMap[I, O]() {
      override def apply(key: I): O = getOrElseUpdate(key, f(key))
    }
  }

  object TrigonometricalOps {
    def sinusoidalSin(yDilatation: Float)(x:Float)(phase: Int)(yTranslation: Int): Int =
      (yDilatation * Math.sin(2 * Math.PI * x + phase)).toInt + yTranslation  //should rename ytranslation to amplitude

    //most used, common and popular sinusoidalSin invocations (for this purpose translated in partially-applied functions)
    def zeroPhasedSinusoidalSin: Function3[Float, Float, Int, Int] = (fl1: Float, fl2: Float, i: Int) => TrigonometricalOps.sinusoidalSin  (fl1) (fl2) (0) (i)

    def zeroYTranslatedSinusoidalSin: Function3[Float, Float, Int, Int] = (fl1: Float, fl2: Float, i: Int) => TrigonometricalOps.sinusoidalSin (fl1) (fl2) (i) (0)

    def oneYTranslatedSinusoidalSin: Function3[Float, Float, Int, Int] = (fl1: Float, fl2: Float, i: Int) => TrigonometricalOps.sinusoidalSin (fl1) (fl2) (i) (1)

    def zeroPhasedOneYTranslatedSinusoidalSin : Function2[Float, Float, Int] = (fl1: Float, fl2: Float) => TrigonometricalOps.sinusoidalSin (fl1) (fl2) (0)  (1)

    object Curried {
      def zeroPhasedSinusoidalSin: Function1[Float, Float => Int => Int] = TrigonometricalOps.zeroPhasedSinusoidalSin.curried
      def zeroYTranslatedSinusoidalSin: Function1[Float, Float => Int => Int] = TrigonometricalOps.zeroYTranslatedSinusoidalSin.curried
      def oneYTranslatedSinusoidalSin: Function1[Float, Float => Int => Int] = TrigonometricalOps.oneYTranslatedSinusoidalSin.curried
      def zeroPhasedOneYTranslatedSinusoidalSin: Function1[Float, Float => Int] =TrigonometricalOps.zeroPhasedOneYTranslatedSinusoidalSin.curried
    }

    /*example of use:
    instead of calling: sinusoidalSin(1f)(2)(0)(1) in many places in our code, call this instead:
    zeroPhasedOneYTranslatedSinusoidalSin(1f)(2)            -------->(reuse)
    */

  }

}



