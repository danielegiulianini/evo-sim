package evo_sim.model

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.EntityBehaviour.Simulable.NeutralBehaviour
import evo_sim.model.EntityBehaviour._
import evo_sim.model.EntityStructure.DomainImpl.{Cooldown, DegradationEffect, Effect, Life, LifeCycle, MovementStrategy, Velocity}
import evo_sim.model.EntityStructure._

object Entities {

  //leaves of model hierarchy
  case class BaseBlob(override val name: String,
                      override val boundingBox: Circle,
                      override val life: Life,
                      override val velocity: Velocity,
                      override val degradationEffect: DegradationEffect[Blob],
                      override val fieldOfViewRadius: Int,
                      override val movementStrategy: MovementStrategy,
                      override val direction: Direction) extends Blob with BaseBlobBehaviour {
    def as[T](f: BaseBlob => T) = f(this)
  }

  object BaseBlob {
    def SlowBlobMapper = (blob: BaseBlob) =>
      SlowBlob(blob.name, blob.boundingBox, blob.life, blob.velocity, blob.degradationEffect,
        blob.fieldOfViewRadius, blob.movementStrategy, blob.direction, Constants.DEF_COOLDOWN, blob.velocity)

    def PoisonBlobMapper = (blob: BaseBlob) =>
      PoisonBlob(blob.name, blob.boundingBox, blob.life, blob.velocity, blob.degradationEffect,
        blob.fieldOfViewRadius, blob.movementStrategy, blob.direction, Constants.DEF_COOLDOWN)
  }

  case class CannibalBlob(override val name: String,
                          override val boundingBox: Circle,
                          override val life: Life,
                          override val velocity: Velocity,
                          override val degradationEffect: DegradationEffect[Blob],
                          override val fieldOfViewRadius: Int,
                          override val movementStrategy: MovementStrategy,
                          override val direction: Direction) extends Blob with CannibalBlobBehaviour {
    def as[T](f: CannibalBlob => T) = f(this)
  }

  object CannibalBlob {
    def SlowBlobMapper = (blob: CannibalBlob) =>
      SlowBlob(blob name, blob boundingBox, blob life, blob velocity, blob degradationEffect,
        blob fieldOfViewRadius, blob movementStrategy, blob direction, Constants DEF_COOLDOWN, blob velocity)

    def PoisonBlobMapper = (blob: CannibalBlob) =>
      PoisonBlob(blob name, blob boundingBox, blob life, blob velocity, blob degradationEffect,
        blob fieldOfViewRadius, blob movementStrategy, blob direction, Constants DEF_COOLDOWN)
  }

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
                        override val movementStrategy: MovementStrategy,
                        override val direction: Direction,
                        override val cooldown: Cooldown) extends BlobWithTemporaryStatus with TempBlobBehaviour

  case class SlowBlob(override val name: String,
                      override val boundingBox: Circle,
                      override val life: Life,
                      override val velocity: Velocity,
                      override val degradationEffect: DegradationEffect[Blob],
                      override val fieldOfViewRadius: Int,
                      override val movementStrategy: MovementStrategy,
                      override val direction: Direction,
                      override val cooldown: Cooldown,
                      initialVelocity: Velocity) extends BlobWithTemporaryStatus with TempBlobBehaviour

}


