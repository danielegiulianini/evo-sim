package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.EntityStructure.DomainImpl.{Cooldown, Velocity}

object Effect {

  private val COOLDOWN_DEFAULT: Cooldown = 3

  private val rand = new java.util.Random()
  private val modifyingPropertyRange = 5
  private val foodEnergy = 10

  // adds 10 to blob life and creates new BaseBlob
  def standardFoodEffect(blob: Blob): Set[SimulableEntity] = {
    val newBlob = BaseBlob(blob.boundingBox, blob.life + foodEnergy, randomValueChange(blob.velocity, modifyingPropertyRange), blob.degradationEffect, randomValueChange(blob.fieldOfViewRadius, modifyingPropertyRange), MovingStrategies.baseMovement)
    Set(newBlob, blob match {
      case b : BaseBlob => BaseBlob(b.boundingBox, b.life + foodEnergy, b.velocity, b.degradationEffect, b.fieldOfViewRadius, b.movementStrategy)
      case b: PoisonBlob => PoisonBlob(b.blob, b.boundingBox, b.poisonCooldown)
      case b: SlowBlob => SlowBlob(b.blob, b.boundingBox, b.slownessCooldown, b.initialVelocity)
    })
  }

  def poisonousFoodEffect(blob: BaseBlob): Set[SimulableEntity] = {
    Set(PoisonBlob(blob, blob.boundingBox, COOLDOWN_DEFAULT))
  }

  // used for static entities
  def neutralEffect(blob: Blob): Set[SimulableEntity] = {
    blob match {
      case b : BaseBlob => Set(b)
      case _ => Set()
    }
  }

  def mudEffect(blob: BaseBlob): Set[SimulableEntity] = {
    val currentVelocity: Velocity = if (blob.velocity > 0) blob.velocity - 1 else blob.velocity
    Set(SlowBlob(blob, blob.boundingBox, COOLDOWN_DEFAULT, blob.velocity))
  }

  /* min = value - range, max = value + range */
  private def randomValueChange(value: Int, range: Int): Int = {
    value + rand.nextInt(range + 1) - range
  }
}
