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
                      override val direction: Direction) extends Blob with BaseBlobBehaviour

  case class CannibalBlob(override val name: String,
                          override val boundingBox: Circle,
                          override val life: Life,
                          override val velocity: Velocity,
                          override val degradationEffect: DegradationEffect[Blob],
                          override val fieldOfViewRadius: Int,
                          override val movementStrategy: MovementStrategy,
                          override val direction: Direction) extends Blob with CannibalBlobBehaviour

  /**
   * Represents a basic [[evo_sim.model.EntityStructure.Food]] implementation.
   *
   * @param name food name
   * @param boundingBox food boundingBox
   * @param degradationEffect food degradationEffect
   * @param life food life
   * @param effect food effect
   */
  case class BaseFood(override val name: String,
                      override val boundingBox: Triangle,
                      override val degradationEffect: DegradationEffect[Food],
                      override val life: Life,
                      override val effect: Effect) extends Food with BaseFoodBehaviour

  /**
   * Represents a basic [[evo_sim.model.EntityStructure.Obstacle]] implementation.
   *
   * @param name obstacle name
   * @param boundingBox obstacle boundingBox
   * @param effect obstacle effect
   */
  case class BaseObstacle(override val name: String,
                          override val boundingBox: Rectangle,
                          override val effect: Effect) extends Obstacle with NeutralBehaviour

  /**
   * Represents a [[evo_sim.model.EntityStructure.Plant]] implementation.
   */
  trait BasePlant extends Plant {
    override val name: String
    override val boundingBox: Rectangle
    override val lifeCycle: LifeCycle
  }

  /**
   * Represents a [[evo_sim.model.EntityStructure.Plant]] implementation that produces [[evo_sim.model.EntityStructure.Food]]s with [[evo_sim.model.Effect.standardFoodEffect]].
   *
   * @param name plant name
   * @param boundingBox plant boundingBox
   * @param lifeCycle plant lifeCycle
   */
  case class StandardPlant(override val name: String,
                           override val boundingBox: Rectangle,
                           override val lifeCycle: LifeCycle) extends BasePlant with StandardPlantBehaviour

  /**
   * Represents a [[evo_sim.model.EntityStructure.Plant]] implementation that produces [[evo_sim.model.EntityStructure.Food]]s with [[evo_sim.model.Effect.reproduceBlobFoodEffect]].
   *
   * @param name plant name
   * @param boundingBox plant boundingBox
   * @param lifeCycle plant lifeCycle
   */
  case class ReproducingPlant(override val name: String,
                              override val boundingBox: Rectangle,
                              override val lifeCycle: LifeCycle) extends BasePlant with ReproducingPlantBehaviour

  /**
   * Represents a [[evo_sim.model.EntityStructure.Plant]] implementation that produces [[evo_sim.model.EntityStructure.Food]]s with [[evo_sim.model.Effect.poisonousFoodEffect]].
   *
   * @param name plant name
   * @param boundingBox plant boundingBox
   * @param lifeCycle plant lifeCycle
   */
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


