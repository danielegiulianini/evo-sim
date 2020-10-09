package evo_sim.model

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.EntityBehaviour.{BaseBlobBehaviour, BaseFoodBehaviour, NeutralBehaviour}
import evo_sim.model.EntityStructure.{Blob, Food, Obstacle, SlowedBlob}
import evo_sim.model.EntityStructure.DomainImpl.{Cooldown, DegradationEffect, Effect, Life, MovementStrategy, Velocity}

object Entities {

  //leaves of model hierarchy
  case class BaseBlob(override val boundingBox: Rectangle,
                      override val life: Life,
                      override val velocity: Velocity,
                      override val degradationEffect: DegradationEffect[Blob],
                      override val fieldOfViewRadius: Int,
                      override val movementStrategy: MovementStrategy) extends Blob with BaseBlobBehaviour

  case class BaseFood(override val boundingBox: Circle,
                      override val degradationEffect: DegradationEffect[Food],
                      override val life: Life,
                      override val effect: Effect) extends Food with BaseFoodBehaviour

  case class BaseObstacle(override val boundingBox: Triangle,
                          override val effect: Effect) extends Obstacle with NeutralBehaviour

  //blobs with temporary status changes
  case class SlowBlob(override val boundingBox: Rectangle,
                      override val life: Life,
                      override val velocity: Velocity,
                      override val degradationEffect: DegradationEffect[Blob],
                      override val fieldOfViewRadius: Int,
                      override val movementStrategy: MovementStrategy,
                      override val slownessCooldown: Cooldown,
                      override val initialVelocity: Int) extends SlowedBlob with BaseBlobBehaviour

}


