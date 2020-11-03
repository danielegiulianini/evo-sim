package evo_sim.model.entities

import evo_sim.model.entities.entityStructure.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.Simulable.NeutralBehaviour
import evo_sim.model.entities.entityBehaviour.EntityBehaviour._
import evo_sim.model.entities.entityStructure.EntityStructure.DomainImpl._
import evo_sim.model.entities.entityStructure.EntityStructure._
import evo_sim.model.entities.entityStructure.effects.DegradationEffect
import evo_sim.model.entities.entityStructure.{BoundingBox, EntityStructure}
import evo_sim.model.entities.entityStructure.movement.{Direction, MovingStrategies}
import evo_sim.model.world.Constants

object Entities {

  //leaves of model hierarchy

  /**
   * Represent a basic [[EntityStructure.Blob]].
   * @param name              blob's name.
   * @param boundingBox       blob's [[evo_sim.model.entities.entityStructure.BoundingBox]].
   * @param life              blob's [[EntityStructure.Domain.Life]].
   * @param velocity          blob's [[EntityStructure.Domain.Velocity]].
   * @param degradationEffect blob's [[EntityStructure.Domain.DegradationEffect]].
   * @param fieldOfViewRadius blob's field of view.
   * @param movementStrategy  blob's [[EntityStructure.Domain.MovementStrategy]].
   * @param direction         blob's [[MovingStrategies]].
   */
  case class BaseBlob(override val name: String,
                      override val boundingBox: Circle,
                      override val life: Life = Constants.DEF_BLOB_LIFE,
                      override val velocity: Velocity = Constants.DEF_BLOB_VELOCITY,
                      override val degradationEffect: DegradationEffect[Blob] = DegradationEffect.standardDegradation,
                      override val fieldOfViewRadius: Int = Constants.DEF_BLOB_FOV_RADIUS,
                      override val movementStrategy: MovementStrategy = MovingStrategies.baseMovement,
                      override val direction: Direction = Direction(Constants.DEF_NEXT_DIRECTION,Constants.DEF_NEXT_DIRECTION)) extends Blob with BaseBlobBehaviour

  /**
   * Represent a cannibal blob [[EntityStructure.Blob]] implementation.
   * This blob can eat other smaller blobs type.
   * @param name blob's name.
   * @param boundingBox blob's [[evo_sim.model.entities.entityStructure.BoundingBox]].
   * @param life blob's [[EntityStructure.Domain.Life]].
   * @param velocity blob's [[EntityStructure.Domain.Velocity]].
   * @param degradationEffect blob's [[EntityStructure.Domain.DegradationEffect]].
   * @param fieldOfViewRadius blob's field of view.
   * @param movementStrategy blob's [[EntityStructure.Domain.MovementStrategy]].
   * @param direction blob's [[MovingStrategies]].
   */
  case class CannibalBlob(override val name: String,
                          override val boundingBox: Circle,
                          override val life: Life = Constants.DEF_BLOB_LIFE,
                          override val velocity: Velocity = Constants.DEF_BLOB_VELOCITY,
                          override val degradationEffect: DegradationEffect[Blob] = DegradationEffect.standardDegradation,
                          override val fieldOfViewRadius: Int = Constants.DEF_BLOB_FOV_RADIUS,
                          override val movementStrategy: MovementStrategy = MovingStrategies.baseMovement,
                          override val direction: Direction = Direction(Constants.DEF_NEXT_DIRECTION,Constants.DEF_NEXT_DIRECTION)) extends Blob with CannibalBlobBehaviour

  /**
   * Represents a basic [[EntityStructure.Food]] implementation.
   *
   * @param name food name
   * @param boundingBox food boundingBox
   * @param degradationEffect food degradationEffect
   * @param life food life
   * @param collisionEffect food effect
   */
  case class BaseFood(override val name: String,
                      override val boundingBox: Triangle,
                      override val degradationEffect: DegradationEffect[Food],
                      override val life: Life,
                      override val collisionEffect: CollisionEffect) extends Food with BaseFoodBehaviour

  /**
   * Represents a basic [[EntityStructure.Obstacle]] implementation.
   *
   * @param name obstacle name
   * @param boundingBox obstacle boundingBox
   * @param collisionEffect obstacle effect
   */
  case class BaseObstacle(override val name: String,
                          override val boundingBox: Rectangle,
                          override val collisionEffect: CollisionEffect) extends Obstacle with NeutralBehaviour

  /**
   * Represents a [[EntityStructure.Plant]] implementation that produces [[EntityStructure.Food]]s with [[evo_sim.model.entities.entityStructure.effects.CollisionEffect.standardFoodEffect]].
   *
   * @param name plant name
   * @param boundingBox plant boundingBox
   * @param lifeCycle plant lifeCycle
   */
  case class StandardPlant(override val name: String,
                           override val boundingBox: Rectangle,
                           override val lifeCycle: LifeCycle) extends Plant with StandardPlantBehaviour

  /**
   * Represents a [[EntityStructure.Plant]] implementation that produces [[EntityStructure.Food]]s with [[evo_sim.model.entities.entityStructure.effects.CollisionEffect.reproduceBlobFoodEffect]].
   *
   * @param name plant name
   * @param boundingBox plant boundingBox
   * @param lifeCycle plant lifeCycle
   */
  case class ReproducingPlant(override val name: String,
                              override val boundingBox: Rectangle,
                              override val lifeCycle: LifeCycle) extends Plant with ReproducingPlantBehaviour

  /**
   * Represents a [[EntityStructure.Plant]] implementation that produces [[EntityStructure.Food]]s with [[evo_sim.model.entities.entityStructure.effects.CollisionEffect.poisonousFoodEffect]].
   *
   * @param name plant name
   * @param boundingBox plant boundingBox
   * @param lifeCycle plant lifeCycle
   */
  case class PoisonousPlant(override val name: String,
                              override val boundingBox: Rectangle,
                              override val lifeCycle: LifeCycle) extends Plant with PoisonousPlantBehaviour

  /**
   * Represent a poisoned blob. This blob takes more damage than standard blobs.
   * After a certain cool down the blob became a BaseBlob
   * @param name blob's name.
   * @param boundingBox blob's [[evo_sim.model.entities.entityStructure.BoundingBox]].
   * @param life blob's [[EntityStructure.Domain.Life]].
   * @param velocity blob's [[EntityStructure.Domain.Velocity]].
   * @param degradationEffect blob's [[EntityStructure.Domain.DegradationEffect]].
   * @param fieldOfViewRadius blob's field of view.
   * @param movementStrategy blob's [[EntityStructure.Domain.MovementStrategy]].
   * @param direction blob's [[MovingStrategies]].
   * @param cooldown blob's [[EntityStructure.Domain.Cooldown]]
   */
  case class PoisonBlob(override val name: String,
                        override val boundingBox: Circle,
                        override val life: Life = Constants.DEF_BLOB_LIFE,
                        override val velocity: Velocity = Constants.DEF_BLOB_VELOCITY,
                        override val degradationEffect: DegradationEffect[Blob] = DegradationEffect.standardDegradation,
                        override val fieldOfViewRadius: Int = Constants.DEF_BLOB_FOV_RADIUS,
                        override val movementStrategy: MovementStrategy = MovingStrategies.baseMovement,
                        override val direction: Direction = Direction(Constants.DEF_NEXT_DIRECTION,Constants.DEF_NEXT_DIRECTION),
                        override val cooldown: Cooldown = Constants.DEF_COOLDOWN) extends BlobWithTemporaryStatus with TemporaryStatusBlobBehaviour

  /**
   * Represent a slowed blob. This blob moves slowly than standard blobs.
   * After a certain cool down the blob became a BaseBlob.
   * @param name blob's name.
   * @param boundingBox blob's [[evo_sim.model.entities.entityStructure.BoundingBox]].
   * @param life blob's [[EntityStructure.Domain.Life]].
   * @param velocity blob's [[EntityStructure.Domain.Velocity]].
   * @param degradationEffect blob's [[EntityStructure.Domain.DegradationEffect]].
   * @param fieldOfViewRadius blob's field of view.
   * @param movementStrategy blob's [[EntityStructure.Domain.MovementStrategy]].
   * @param direction blob's [[MovingStrategies]].
   * @param cooldown blob's [[EntityStructure.Domain.Cooldown]]
   * @param initialVelocity blob's initial [[EntityStructure.Domain.Velocity]]. When the slow effect ends
   */
  case class SlowBlob(override val name: String,
                      override val boundingBox: Circle,
                      override val life: Life = Constants.DEF_BLOB_LIFE,
                      override val velocity: Velocity = Constants.DEF_BLOB_VELOCITY,
                      override val degradationEffect: DegradationEffect[Blob] = DegradationEffect.standardDegradation,
                      override val fieldOfViewRadius: Int = Constants.DEF_BLOB_FOV_RADIUS,
                      override val movementStrategy: MovementStrategy = MovingStrategies.baseMovement,
                      override val direction: Direction = Direction(Constants.DEF_NEXT_DIRECTION,Constants.DEF_NEXT_DIRECTION),
                      override val cooldown: Cooldown = Constants.DEF_COOLDOWN,
                      initialVelocity: Velocity) extends BlobWithTemporaryStatus with TemporaryStatusBlobBehaviour

}
