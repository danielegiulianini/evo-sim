package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.view.swing.SwingGUI

case class World(width: Int, //to move in environment?
                 height: Int, //to move in environment?
                 currentIteration: Int,
                 entities: Set[SimulableEntity],
                 totalIterations : Int = 10, //to move in environment?
                )

//companion object
object World {
  def worldCreated(env: Environment): World = {
    val blobs: Set[BaseBlob] = Iterator.fill(env.initialBlobNumber)(BaseBlob(
      boundingBox = BoundingBox.Circle.apply(point = Point2D(20, 15), radius = 5),
      life = 100,
      velocity = 50,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = 10,
      movementStrategy = MovingStrategies.baseMovement)).toSet

    val foods: Set[BaseFood] = Iterator.fill(env.initialFoodNumber)(BaseFood(
      boundingBox = BoundingBox.Triangle.apply(point = Point2D(50, 50), height = 5),
      degradationEffect = DegradationEffect.foodDegradation,
      life = 100,
      effect = Effect.standardFoodEffect)).toSet

    val obstacles: Set[BaseObstacle] = Iterator.fill(env.initialObstacleNumber)(BaseObstacle(
      boundingBox = BoundingBox.Rectangle(point = Point2D(80, 80), width = 5, height = 3),
      effect = Effect.neutralEffect)).toSet

    val entities: Set[SimulableEntity] = blobs ++ foods ++ obstacles

    World(width = 100, height = 100, currentIteration = 0, entities = entities)
  }
}



