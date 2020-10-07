package evo_sim.model

import evo_sim.model.BoundingBoxShape.{Circle, Rectangle, Triangle}
import evo_sim.model.EntityBehaviour.{BaseFoodBehaviour, BlobBehaviour}
import evo_sim.model.EntityStructure.{Blob, Food, Obstacle}
import evo_sim.model.EntityStructure.DomainImpl.{DegradationEffect, Effect, Life, MovementStrategy}

object Entities {

  //leaves of model hierarchy
  case class BaseBlob(override val boundingBox: Rectangle,
                      override val life: Int,
                      override val velocity: Int,
                      override val degradationEffect: DegradationEffect[Blob],
                      override val fieldOfViewRadius: Int,
                      override val movementStrategy: MovementStrategy) extends Blob with BlobBehaviour

  case class BaseFood(override val boundingBox: Circle,
                      override val degradationEffect: DegradationEffect[Food],
                      override val life: Life,
                      override val effect: Effect) extends Food with BaseFoodBehaviour

  case class BaseObstacle(override val boundingBox: Triangle,
                          override val effect: Effect) extends Obstacle

}


