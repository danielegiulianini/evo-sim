package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World.DayPhase.DayPhase

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

  def worldCreated(env: Environment): World = {
    val iterationsPerDay = 100
    val worldWidth = 1280
    val worldHeight = 720

    def randomPosition() = Point2D(new scala.util.Random().nextInt(worldWidth + 1), new scala.util.Random().nextInt(worldHeight + 1))

    val blobs: Set[BaseBlob] = Iterator.tabulate(env.initialBlobNumber)(i => BaseBlob(
      name = "blob" + i,
      boundingBox = BoundingBox.Circle.apply(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      movementDirection = 0,
      stepToNextDirection = 20)).toSet

    val foods: Set[BaseFood] = Iterator.tabulate(env.initialFoodNumber)(i => BaseFood(
      name = "food" + i,
      boundingBox = BoundingBox.Triangle.apply(point = randomPosition(), height = Constants.DEF_FOOD_HEIGHT),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.standardFoodEffect)).toSet

    val stones: Set[BaseObstacle] = Iterator.tabulate((env.initialObstacleNumber / 2).ceil.toInt)(i => BaseObstacle(
      name = "obstacle" + i,
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_STONE_WIDTH, height = Constants.DEF_STONE_HEIGHT),
      effect = Effect.neutralEffect)).toSet

    val puddles: Set[BaseObstacle] = Iterator.tabulate((env.initialObstacleNumber / 2).floor.toInt)(i => BaseObstacle(
      name = "obstacle" + i,
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_PUDDLE_WIDTH, height = Constants.DEF_PUDDLE_HEIGHT),
      effect = Effect.mudEffect)).toSet

    val entities: Set[SimulableEntity] = blobs ++ foods ++ stones ++ puddles

    World(temperature = env.temperature, luminosity = env.luminosity, width = worldWidth, height = worldHeight,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * iterationsPerDay)
  }

  case class EnvironmentParameters(temperature: Int, luminosity: Int)

  object DayPhase extends Enumeration {
    type DayPhase = Value
    val Morning, Afternoon, Evening, Night = Value
  }

  def worldEnvironmentUpdated(world: World): EnvironmentParameters = {

    val phaseDuration: Int = Constants.ITERATIONS_PER_DAY / DayPhase.values.size

    def asDayPhase(iteration: Int): DayPhase = iteration % Constants.ITERATIONS_PER_DAY match {
      case i if phaseDuration >= i => DayPhase.Night
      case i if phaseDuration + 1 to phaseDuration * 2 contains i => DayPhase.Morning
      case i if phaseDuration * 2 + 1 to phaseDuration * 3 contains i => DayPhase.Afternoon
      case i if phaseDuration * 3 < i => DayPhase.Evening
    }

    val currentDayPhase = asDayPhase(world.currentIteration)
    val nextDayPhase = asDayPhase(world.currentIteration + 1)

    (currentDayPhase != nextDayPhase, nextDayPhase) match {
      case (true, DayPhase.Night) => EnvironmentParameters((world.luminosity * 0.20).toInt, world.temperature - 12)
      case (true, DayPhase.Morning) => EnvironmentParameters(world.luminosity * 4, world.temperature + 8)
      case (true, DayPhase.Afternoon) => EnvironmentParameters((world.luminosity * 1.5).toInt, world.temperature + 10)
      case (true, DayPhase.Evening) => EnvironmentParameters((world.luminosity * 0.75).toInt, world.temperature - 6)
      case _ => EnvironmentParameters(world.luminosity, world.temperature)
    }
  }

}



