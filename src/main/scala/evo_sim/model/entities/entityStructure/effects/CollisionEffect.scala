package evo_sim.model.entities.entityStructure.effects

import evo_sim.model.entities.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.entities.entityBehaviour.BlobEntityHelper
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.BoundingBox.Circle
import evo_sim.model.entities.entityStructure.EntityStructure.Blob
import evo_sim.model.world.Constants
import evo_sim.utils.Counter

/**
 * Singleton object that provides different collision effect implementations to be used by object that extend [[EntityStructure.Effectful]].
 */
object CollisionEffect {

  /**
   * Returns a set with a copy of the [[EntityStructure.Blob]] given as input with life incremented by [[Constants.DEF_FOOD_ENERGY]].
   *
   * @param blob a [[EntityStructure.Blob]] subjected to the effect
   * @return a set with the blob produced by the effect
   */
  def standardFoodEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case b: BaseBlob => Set(b.copy(life = blob.life + Constants.DEF_FOOD_ENERGY))
    case b: CannibalBlob => Set(b.copy(life = blob.life + Constants.DEF_FOOD_ENERGY))
    case _ => Set()
  }

  /**
   * Returns a set with a copy of the [[EntityStructure.Blob]] given as input with life incremented by [[Constants.DEF_FOOD_ENERGY]] and a new [[EntityStructure.Blob]] with properties based of the other blob.
   *
   * @param blob a [[EntityStructure.Blob]] subjected to the effect
   * @return a set with the blobs produced by the effect
   */
  def reproduceBlobFoodEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy(life = base.life + Constants.DEF_FOOD_ENERGY), createChild(base))
    case cannibal: CannibalBlob => Set(cannibal.copy(life = cannibal.life + Constants.DEF_FOOD_ENERGY), createChild(cannibal))
    case poison: PoisonBlob => Set(poison.copy())
    case slow: SlowBlob => Set(slow.copy())
    case _ => Set()
  }

  /**
   * Returns a set with a copy of the [[EntityStructure.Blob]] given as input converted to [[Entities.PoisonBlob]].
   *
   * @param blob a [[EntityStructure.Blob]] subjected to the effect
   * @return a set with the blob produced by the effect
   */
  def poisonousFoodEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case _: BaseBlob => Set(BlobEntityHelper.fromBlobToTemporaryBlob(blob, Constants.POISONBLOB_TYPE))
    case _: CannibalBlob => Set(BlobEntityHelper.fromBlobToTemporaryBlob(blob, Constants.POISONBLOB_TYPE))
    case _ => Set()
  }

  /**
   * Returns a set with a copy of the [[EntityStructure.Blob]] given as input.
   *
   * @param blob a [[EntityStructure.Blob]]  subjected to the effect
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
   * Returns a set with the [[EntityStructure.Blob]] given as input converted to [[Entities.SlowBlob]].
   *
   * @param blob a [[EntityStructure.Blob]] subjected to the effect
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
   * Returns a set with a copy of the [[EntityStructure.Blob]] given as input with life decreased by [[Constants.DEF_DAMAGE]].
   *
   * @param blob a [[EntityStructure.Blob]] subjected to the effect
   * @return a set with the blob produced by the effect
   */
  def damageEffect[A <: Blob](blob: A): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy(life = base.life - Constants.DEF_DAMAGE))
    case base: CannibalBlob => Set(base.copy(life = base.life - Constants.DEF_DAMAGE))
    case _ => Set()
  }

  /**
   * Returns a [[EntityStructure.Blob]] with properties based off the blob given as input ones.
   *
   * @param blob a [[EntityStructure.Blob]]
   * @return a [[EntityStructure.Blob]] with properties based off the blob given as input ones.
   */
  private def createChild[A <: Blob](blob: A): SimulableEntity =
    new java.util.Random().nextInt(2) match {
      case 0 => BaseBlob(name = blob.name + "-son" + Counter.nextValue,
        boundingBox = Circle(blob.boundingBox.point, randomValueChange(Constants.DEF_BLOB_RADIUS).max(Constants.MIN_BLOB_RADIUS)),
        fieldOfViewRadius = randomValueChange(Constants.DEF_BLOB_FOV_RADIUS).max(Constants.MIN_BLOB_FOV_RADIUS))
      case 1 => CannibalBlob(name = blob.name + "-son" + Counter.nextValue,
        boundingBox = Circle(blob.boundingBox.point, randomValueChange(Constants.DEF_BLOB_RADIUS).max(Constants.MIN_BLOB_RADIUS)),
        fieldOfViewRadius = randomValueChange(Constants.DEF_BLOB_FOV_RADIUS).max(Constants.MIN_BLOB_FOV_RADIUS))
    }

  /** Returns a value with variable range from an initial value.
   *
   * @param value value that determines the range
   * @return a value between value - range and value + range
   */
  private def randomValueChange(value: Int): Int = {
    val max: Int = (value * 1.5).toInt
    val min: Int = (value * 0.8).toInt
    new java.util.Random().nextInt(max + 1 - min) - min
  }

}
