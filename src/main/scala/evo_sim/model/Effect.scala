package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.EntityStructure.DomainImpl.Velocity

object Effect {

  // adds 10 to blob life
  def standardFoodEffect(blob: Blob): Set[SimulableEntity] = {
    val updatedBlob = BaseBlob(blob.name, blob.boundingBox, blob.life + Constants.DEF_FOOD_ENERGY,
      blob.velocity,
      blob.degradationEffect,
      blob.fieldOfViewRadius,
      MovingStrategies.baseMovement, blob.direction)
    Set(updatedBlob)
  }

  def reproduceBlobFoodEffect(blob: Blob): Set[SimulableEntity] = {
    val newBlob = BaseBlob(blob.name+"-son",
      blob.boundingBox,
      Constants.DEF_BLOB_LIFE,
      randomValueChange(blob.velocity, Constants.DEF_MOD_PROP_RANGE), blob.degradationEffect,
      randomValueChange(blob.fieldOfViewRadius, Constants.DEF_MOD_PROP_RANGE),
      MovingStrategies.baseMovement, Direction(blob.direction.angle, Constants.NEXT_DIRECTION))
    Set(newBlob, blob match {
      case b : BaseBlob => BaseBlob(b.name, b.boundingBox, b.life + Constants.DEF_FOOD_ENERGY,
        b.velocity, b.degradationEffect, b.fieldOfViewRadius, b.movementStrategy, b.direction)
      case b: PoisonBlob => b.copy()
      case b: SlowBlob => b.copy()
    })
  }

  def poisonousFoodEffect(blob: Blob): Set[SimulableEntity] = {
    Set(PoisonBlob(blob.name, blob.boundingBox, blob.life, blob.velocity, blob.degradationEffect,
      blob.fieldOfViewRadius, blob.movementStrategy, blob.direction, Constants.DEF_COOLDOWN))
  }

  // used for static entities
  def neutralEffect(blob: Blob): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy())
    case poison: PoisonBlob => Set(poison.copy())
    case slow: SlowBlob => Set(slow.copy())
    case _ => Set()
  }

  def mudEffect(blob: Blob): Set[SimulableEntity] = {
    //val currentVelocity: Velocity = if (blob.velocity > 0) blob.velocity - 1 else blob.velocity
    blob match {
      case _ : BaseBlob => Set(SlowBlob(blob.name, blob.boundingBox, blob.life, blob.velocity, blob.degradationEffect,
        blob.fieldOfViewRadius, blob.movementStrategy, blob.direction, Constants.DEF_COOLDOWN, blob.velocity))
      case _ => Set()
    }
  }

  def damageEffect(blob: Blob): Set[SimulableEntity] = blob match {
    case base: BaseBlob => Set(base.copy(life = base.life - Constants.DEF_DAMAGE))
    case _ => Set()
    }

  /* min = value - range, max = value + range */
  private def randomValueChange(value: Int, range: Int): Int = {
    value + new java.util.Random().nextInt(range * 2 + 1) - range
  }
}
