package evo_sim.model

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.EntityBehaviour.Simulable.NeutralBehaviour
import evo_sim.model.EntityBehaviour._
import evo_sim.model.EntityStructure.DomainImpl.{Cooldown, DegradationEffect, Effect, Gender, Life, LifeCycle, MovementStrategy, Velocity}
import evo_sim.model.EntityStructure._

import scala.reflect.ClassTag

object Entities {

  //leaves of model hierarchy
  case class BaseBlob(override val name: String,
                      override val boundingBox: Circle,
                      override val life: Life,
                      override val velocity: Velocity,
                      override val degradationEffect: DegradationEffect[Blob],
                      override val fieldOfViewRadius: Int,
                      override val gender: Gender,
                      override val movementStrategy: MovementStrategy,
                      override val direction: Direction) extends Blob with BaseBlobBehaviour

  case class CannibalBlob(override val name: String,
                          override val boundingBox: Circle,
                          override val life: Life,
                          override val velocity: Velocity,
                          override val degradationEffect: DegradationEffect[Blob],
                          override val fieldOfViewRadius: Int,
                          override val gender: Gender,
                          override val movementStrategy: MovementStrategy,
                          override val direction: Direction) extends Blob with CannibalBlobBehaviour

  case class BaseFood(override val name: String,
                      override val boundingBox: Triangle,
                      override val degradationEffect: DegradationEffect[Food],
                      override val life: Life,
                      override val effect: Effect) extends Food with BaseFoodBehaviour

  case class BaseObstacle(override val name: String,
                          override val boundingBox: Rectangle,
                          override val effect: Effect) extends Obstacle with NeutralBehaviour

  trait BasePlant extends Plant {
    override val name: String
    override val boundingBox: Rectangle
    override val lifeCycle: LifeCycle
  }

  case class StandardPlant(override val name: String,
                           override val boundingBox: Rectangle,
                           override val lifeCycle: LifeCycle) extends BasePlant with StandardPlantBehaviour

  case class ReproducingPlant(override val name: String,
                              override val boundingBox: Rectangle,
                              override val lifeCycle: LifeCycle) extends BasePlant with ReproducingPlantBehaviour

  case class PoisonousPlant(override val name: String,
                              override val boundingBox: Rectangle,
                              override val lifeCycle: LifeCycle) extends BasePlant with PoisonousPlantBehaviour

  case class PoisonBlob(override val name: String,
                        override val boundingBox: Circle,
                        override val life: Life,
                        override val velocity: Velocity,
                        override val degradationEffect: DegradationEffect[Blob],
                        override val fieldOfViewRadius: Int,
                        override val gender: Gender,
                        override val movementStrategy: MovementStrategy,
                        override val direction: Direction,
                        override val cooldown: Cooldown) extends BlobWithTemporaryStatus with TempBlobBehaviour

  case class SlowBlob(override val name: String,
                      override val boundingBox: Circle,
                      override val life: Life,
                      override val velocity: Velocity,
                      override val degradationEffect: DegradationEffect[Blob],
                      override val fieldOfViewRadius: Int,
                      override val gender: Gender,
                      override val movementStrategy: MovementStrategy,
                      override val direction: Direction,
                      override val cooldown: Cooldown,
                      initialVelocity: Velocity) extends BlobWithTemporaryStatus with TempBlobBehaviour

}


