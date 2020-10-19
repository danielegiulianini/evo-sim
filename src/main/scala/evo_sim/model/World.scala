package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityBehaviour.SimulableEntity

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
    val blobs: Set[SimulableEntity] = Iterator.tabulate(env.initialBlobNumber)(i => BaseBlob(
      name = "blob" + i,
      boundingBox = BoundingBox.Circle(point = randomPosition(), radius = Constants.DEF_BLOB_RADIUS),
      life = Constants.DEF_BLOB_LIFE,
      velocity = Constants.DEF_BLOB_VELOCITY,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = Constants.DEF_BLOB_FOW_RADIUS,
      movementStrategy = MovingStrategies.baseMovement,
      direction = Direction(0, 20))).toSet

    val foods: Set[SimulableEntity] = Iterator.tabulate(env.initialFoodNumber)(i => BaseFood(
      name = "food" + i,
      boundingBox = BoundingBox.Triangle(point = randomPosition(), height = Constants.DEF_FOOD_HEIGHT),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Constants.DEF_FOOD_LIFE,
      effect = Effect.standardFoodEffect)).toSet


    val stones: Set[BaseObstacle] = Iterator.tabulate((env.initialObstacleNumber / 2).ceil.toInt)(i => BaseObstacle(
      name = "stone" + i,
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_STONE_WIDTH, height = Constants.DEF_STONE_HEIGHT),
      effect = Effect.damageEffect)).toSet


    val puddles: Set[BaseObstacle] = Iterator.tabulate((env.initialObstacleNumber / 2).floor.toInt)(i => BaseObstacle(
      name = "puddle" + i,
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = Constants.DEF_PUDDLE_WIDTH, height = Constants.DEF_PUDDLE_HEIGHT),
      effect = Effect.mudEffect)).toSet

    val entities: Set[SimulableEntity] = blobs ++ foods ++ stones ++ puddles

    World(temperature = env.temperature, luminosity = env.luminosity, width = Constants.WORLD_WIDTH, height = Constants.WORLD_HEIGHT,
      currentIteration = 0, entities = entities, totalIterations = env.daysNumber * Constants.ITERATIONS_PER_DAY)
  }

  case class EnvironmentParameters(luminosity: Int, temperature: Int)

  def worldEnvironmentUpdated(world: World): EnvironmentParameters = {
    def updatedLuminosity(luminosity: Int, currentIteration: Int) =
      luminosity + ((1 + 1 / 32f) * Math.sin(2 * Math.PI * currentIteration / Constants.ITERATIONS_PER_DAY)).toInt

    def updatedTemperature(temperature: Int, currentIteration: Int) =
      temperature + ((1 + 1 / 64f) * Math.sin(2 * Math.PI * currentIteration / Constants.ITERATIONS_PER_DAY)).toInt

    EnvironmentParameters(updatedLuminosity(world.luminosity, world.currentIteration),
      updatedTemperature(world.temperature, world.currentIteration))
  }

}



