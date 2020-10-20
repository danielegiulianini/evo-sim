package evo_sim.model

import evo_sim.model.Entities.{BaseBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.EntityStructure.DomainImpl.Velocity

object Effect {

  // adds 10 to blob life
  def standardFoodEffect(blob: Blob): Set[SimulableEntity] = {
    val updatedBlob = BaseBlob(blob.name, blob.boundingBox, blob.life + Constants.DEF_FOOD_ENERGY,
      randomValueChange(blob.velocity, Constants.DEF_MOD_PROP_RANGE), blob.degradationEffect,
      randomValueChange(blob.fieldOfViewRadius, Constants.DEF_MOD_PROP_RANGE),
      MovingStrategies.baseMovement, blob.direction/*blob.movementDirection, blob.stepToNextDirection*/)
    Set(updatedBlob)
  }

  def reproduceBlobFoodEffect(blob: Blob): Set[SimulableEntity] = {
    val newBlob = BaseBlob(blob.name+"-son", blob.boundingBox, blob.life + Constants.DEF_FOOD_ENERGY,
      randomValueChange(blob.velocity, Constants.DEF_MOD_PROP_RANGE), blob.degradationEffect,
      randomValueChange(blob.fieldOfViewRadius, Constants.DEF_MOD_PROP_RANGE),
      MovingStrategies.baseMovement, blob.direction/*blob.movementDirection, blob.stepToNextDirection*/)
    Set(newBlob, blob match {
      case b : BaseBlob => BaseBlob(b.name, b.boundingBox, b.life + Constants.DEF_FOOD_ENERGY,
        b.velocity, b.degradationEffect, b.fieldOfViewRadius, b.movementStrategy, b.direction/*b.movementDirection, b.stepToNextDirection*/)
      case b: PoisonBlob => PoisonBlob(b.name, b.blob, b.boundingBox, b.cooldown)
      case b: SlowBlob => SlowBlob(b.name, b.blob, b.boundingBox, b.cooldown, b.initialVelocity)
    })
  }

  def poisonousFoodEffect(blob: Blob): Set[SimulableEntity] = {
    Set(PoisonBlob(blob.name, blob, blob.boundingBox, Constants.DEF_COOLDOWN))
  }

  // used for static entities
  def neutralEffect(blob: Blob): Set[SimulableEntity] = {
    blob match {
      case b : BaseBlob => Set(b)
      case _ => Set()
    }
  }

  def mudEffect(blob: Blob): Set[SimulableEntity] = {
    //val currentVelocity: Velocity = if (blob.velocity > 0) blob.velocity - 1 else blob.velocity
    blob match {
      case b : BaseBlob => Set(SlowBlob(b.name, b, b.boundingBox, Constants.DEF_COOLDOWN, b.velocity))
      case _ => Set()
    }
  }

  def damageEffect(blob: Blob): Set[SimulableEntity] = blob match {
    case b: BaseBlob => Set(BaseBlob(b.name, b.boundingBox, b.life - Constants.DEF_DAMAGE, b.velocity,
      b.degradationEffect, b.fieldOfViewRadius, b.movementStrategy, b.direction))
    case _ => Set()
    }

  /* min = value - range, max = value + range */
  private def randomValueChange(value: Int, range: Int): Int = {
    value + new java.util.Random().nextInt(range * 2 + 1) - range
  }
}
