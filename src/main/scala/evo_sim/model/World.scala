package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, BaseFood, BaseObstacle}
import evo_sim.model.EntityBehaviour.SimulableEntity

case class World(width: Int, height: Int, currentIteration: Int, entities: Set[SimulableEntity])

//companion object
object World {
  def worldCreated(env: Environment): World = {
    /*
    val blobs: Set[BaseBlob] = Iterator.fill(env.initialBlobNumber)(BaseBlob(
      boundingBox = BoundingBoxShape.Rectangle.apply(point = (20, 15), width = 10, height = 10),
      life = 100,
      velocity = 50,
      degradationEffect = DegradationEffect.standardDegradation,
      fieldOfViewRadius = 10,
      movementStrategy = null /*MovingStrategies.baseMovement?*/)).toSet

    val foods: Set[BaseFood] = Iterator.fill(env.initialFoodNumber)(BaseFood(
      boundingBox = BoundingBoxShape.Circle.apply(point = (100, 100), radius = 10),
      degradationEffect = DegradationEffect.standardDegradation,
      life = 100,
      effect = null /*Effect.standardFoodEffect*/)).toSet

    val obstacles: Set[BaseObstacle] = Iterator.fill(env.initialObstacleNumber)(BaseObstacle(
      boundingBox = BoundingBoxShape.Triangle(point = (200, 200), height = 15),
      effect = null /*Effect.neutralEffect*/)).toSet
     */

    val entities: Set[SimulableEntity] = ??? //blobs ++ foods ++ obstacles

    World(width = 100, height = 100, currentIteration = 0, entities = entities)
  }
}



