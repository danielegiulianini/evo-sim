package evo_sim.model

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.EntityBehaviour.{BaseBlobBehaviour, BaseFoodBehaviour, NeutralBehaviour, Simulable, SimulableEntity, TempBlobBehaviour}
import evo_sim.model.EntityStructure.{Blob, Entity, Food, Obstacle, BlobWithTemporaryStatus}
import evo_sim.model.EntityStructure.DomainImpl.{Cooldown, DegradationEffect, Effect, Life, MovementStrategy, Velocity}

object Entities {

  //leaves of model hierarchy
  case class BaseBlob(override val boundingBox: Circle,
                      override val life: Life,
                      override val velocity: Velocity,
                      override val degradationEffect: DegradationEffect[Blob],
                      override val fieldOfViewRadius: Int,
                      override val movementStrategy: MovementStrategy) extends Blob with BaseBlobBehaviour

  case class BaseFood(override val boundingBox: Triangle,
                      override val degradationEffect: DegradationEffect[Food],
                      override val life: Life,
                      override val effect: Effect) extends Food with BaseFoodBehaviour

  case class BaseObstacle(override val boundingBox: Rectangle,
                          override val effect: Effect) extends Obstacle with NeutralBehaviour

  //blobs with temporary status changes refactored
  trait TempBlob extends BlobWithTemporaryStatus with Entity with Simulable

  case class PoisonBlob(  override val blob: BaseBlob,
                          override val boundingBox: Circle,
                          poisonCooldown: Cooldown) extends TempBlob with TempBlobBehaviour

  case class SlowBlob(    override val blob: BaseBlob,
                          override val boundingBox: Circle,
                          slownessCooldown: Cooldown,
                          initialVelocity: Velocity) extends TempBlob with TempBlobBehaviour
}


