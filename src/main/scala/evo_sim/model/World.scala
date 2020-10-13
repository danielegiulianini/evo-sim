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
    val blobs: Set[SimulableEntity] = Iterator.fill(env.initialBlobNumber)(BaseBlob(
      boundingBox = BoundingBox.Circle.apply(point = Point2D(640, 360), radius = 5),
      life = 100,
      velocity = 50,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = 10,
      movementStrategy = MovingStrategies.baseMovement)).toSet

    val foods: Set[SimulableEntity] = Iterator.fill(env.initialFoodNumber)(BaseFood(
      boundingBox = BoundingBox.Triangle.apply(point = Point2D(700, 400), height = 10),
      degradationEffect = DegradationEffect.foodDegradation,
      life = 100,
      effect = Effect.standardFoodEffect)).toSet

    val obstacles: Set[SimulableEntity] = Iterator.fill(env.initialObstacleNumber)(BaseObstacle(
      boundingBox = BoundingBox.Rectangle(point = Point2D(600, 300), width = 15, height = 12),
      effect = Effect.neutralEffect)).toSet

    val entities = blobs ++ foods ++ obstacles

    World(width = 1280, height = 720, currentIteration = 0, entities = entities, totalIterations = env.daysNumber * iterationsPerDay)
  }

  val iterationsPerDay = 5
}



