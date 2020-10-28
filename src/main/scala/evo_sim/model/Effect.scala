package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.Utils._

/**
 * Singleton object that provides different effect implementations to be used by object that extend [[evo_sim.model.EntityStructure.Effectful]] .
 *
 */
object Effect {

  /**
   * Returns a set with a copy of the [[evo_sim.model.EntityStructure.Blob]] given as input with life incremented by [[Constants.DEF_FOOD_ENERGY]].
   *
   * @param blob a [[evo_sim.model.EntityStructure.Blob]] subjected to the effect
   * @return a set with the blob produced by the effect
   */
  def standardFoodEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case b: BaseBlob => Set(b.copy(life = blob.life + Constants.DEF_FOOD_ENERGY))
    case b: CannibalBlob => Set(b.copy(life = blob.life + Constants.DEF_FOOD_ENERGY))
  }

  /**
   * Returns a set with a copy of the [[evo_sim.model.EntityStructure.Blob]] given as input with life incremented by [[evo_sim.model.Constants.DEF_FOOD_ENERGY]] and a new [[evo_sim.model.EntityStructure.Blob]] with properties based of the other blob.
   *
   * @param blob a [[evo_sim.model.EntityStructure.Blob]] subjected to the effect
   * @return a set with the blobs produced by the effect
   */
  def reproduceBlobFoodEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy(life = base.life + Constants.DEF_FOOD_ENERGY), createChild(base))
    case cannibal: CannibalBlob => Set(cannibal.copy(life = cannibal.life + Constants.DEF_FOOD_ENERGY), createChild(cannibal))
    case poison: PoisonBlob => Set(poison.copy())
    case slow: SlowBlob => Set(slow.copy())
  }

  /**
   * Returns a set with a copy of the [[evo_sim.model.EntityStructure.Blob]] given as input converted to [[evo_sim.model.Entities.PoisonBlob]].
   *
   * @param blob a [[evo_sim.model.EntityStructure.Blob]] subjected to the effect
   * @return a set with the blob produced by the effect
   */
  def poisonousFoodEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case _: BaseBlob => Set(BlobEntityHelper.fromBlobToTemporaryBlob(blob, Constants.POISONBLOB_TYPE))
    case _: CannibalBlob => Set(BlobEntityHelper.fromBlobToTemporaryBlob(blob, Constants.POISONBLOB_TYPE))
    case _ => Set()
  }

  /**
   * Returns a set with a copy of the [[evo_sim.model.EntityStructure.Blob]] given as input.
   *
   * @param blob a [[evo_sim.model.EntityStructure.Blob]]  subjected to the effect
   * @return a set with the blob produced by the effect
   */
  def neutralEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy())
    case base: CannibalBlob => Set(base.copy())
    case poison: PoisonBlob => Set(poison.copy())
    case slow: SlowBlob => Set(slow.copy())
    case _ => Set()
  }

  /**
   * Returns a set with the [[evo_sim.model.EntityStructure.Blob]] given as input converted to [[evo_sim.model.Entities.SlowBlob]].
   *
   * @param blob a [[evo_sim.model.EntityStructure.Blob]] subjected to the effect
   * @return a set with the blob produced by the effect
   */
  def slowEffect[A <: Blob](blob: A): Set[SimulableEntity] = {
    blob match {
      case _: BaseBlob => Set(BlobEntityHelper.fromBlobToTemporaryBlob(blob, Constants.SLOWBLOB_TYPE))
      case _: CannibalBlob => Set(BlobEntityHelper.fromBlobToTemporaryBlob(blob, Constants.SLOWBLOB_TYPE))
      case _ => Set()
    }
  }

  /**
   * Returns a set with a copy of the [[evo_sim.model.EntityStructure.Blob]] given as input with life decreased by [[Constants.DEF_DAMAGE]].
   *
   * @param blob a [[evo_sim.model.EntityStructure.Blob]] subjected to the effect
   * @return a set with the blob produced by the effect
   */
  def damageEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy(life = base.life - Constants.DEF_DAMAGE))
    case base: CannibalBlob => Set(base.copy(life = base.life - Constants.DEF_DAMAGE))
    case _ => Set()
  }

  /**
   * Returns a [[evo_sim.model.EntityStructure.Blob]] with properties based off the blob given as input ones.
   *
   * @param blob a [[evo_sim.model.EntityStructure.Blob]]
   * @return a [[evo_sim.model.EntityStructure.Blob]] with properties based off the blob given as input ones.
   */
  private def createChild[A <: Blob](blob: A): SimulableEntity = blob match{
    case _: BaseBlob => BaseBlob(blob.name + "-son" + nextValue,
      Circle(blob.boundingBox.point, randomValueChange(Constants.DEF_BLOB_RADIUS)), Constants.DEF_BLOB_LIFE,
      Constants.DEF_BLOB_VELOCITY, blob.degradationEffect, randomValueChange(Constants.DEF_BLOB_FOW_RADIUS),
      MovingStrategies.baseMovement, Direction(blob.direction.angle, Constants.DEF_NEXT_DIRECTION))
    case _: CannibalBlob => CannibalBlob(blob.name + "-son" + nextValue,
      Circle(blob.boundingBox.point, randomValueChange(Constants.DEF_BLOB_RADIUS)), Constants.DEF_BLOB_LIFE,
      Constants.DEF_BLOB_VELOCITY, blob.degradationEffect, randomValueChange(Constants.DEF_BLOB_FOW_RADIUS),
      MovingStrategies.baseMovement, Direction(blob.direction.angle, Constants.DEF_NEXT_DIRECTION))
    }

}
