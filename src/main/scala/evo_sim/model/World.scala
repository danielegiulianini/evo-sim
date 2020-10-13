package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityBehaviour.SimulableEntity

case class World(width: Int, //to move in environment?
                 height: Int, //to move in environment?
                 currentIteration: Int,
                 entities: Set[SimulableEntity],
                 totalIterations: Int, //to move in environment?
                )

//companion object
object World {
  def worldCreated(env: Environment): World = {

    val iterationsPerDay = 1000
    val worldWidth = 1280
    val worldHeight = 720

    def randomPosition() = Point2D(new scala.util.Random().nextInt(worldWidth + 1), new scala.util.Random().nextInt(worldHeight + 1))

    val blobs: Set[SimulableEntity] = Iterator.fill(env.initialBlobNumber)(BaseBlob(
      boundingBox = BoundingBox.Circle.apply(point = randomPosition(), radius = 5),
      life = Integer.MAX_VALUE,
      velocity = 50,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = 10,
      movementStrategy = MovingStrategies.baseMovement)).toSet


    val foods: Set[SimulableEntity] = Iterator.fill(env.initialFoodNumber)(BaseFood(
      boundingBox = BoundingBox.Triangle.apply(point = randomPosition(), height = 10),
      degradationEffect = DegradationEffect.foodDegradation,
      life = Integer.MAX_VALUE,
      effect = Effect.standardFoodEffect)).toSet


    val obstacles: Set[SimulableEntity] = Iterator.fill(env.initialObstacleNumber)(BaseObstacle(
      boundingBox = BoundingBox.Rectangle(point = randomPosition(), width = 15, height = 12),
      effect = Effect.neutralEffect)).toSet

    val entities = blobs ++ foods ++ obstacles

    World(width = worldWidth, height = worldHeight, currentIteration = 0, entities = entities, totalIterations = env.daysNumber * iterationsPerDay)
  }
}



