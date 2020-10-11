package evo_sim.model

import evo_sim.model.BoundingBox.Rectangle
import evo_sim.model.Entities.{BaseBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.EntityStructure.DomainImpl.{Cooldown, DegradationEffect, Life, MovementStrategy, Velocity}

object Effect {

  private val COOLDOWN_DEFAULT: Cooldown = 3

  private val rand = new java.util.Random()
  private val modifyingPropertyRange = 5
  private val foodEnergy = 10

  // adds 10 to blob life
  def standardFoodEffect(blob: Blob): Set[SimulableEntity] = {
    val newBlob = randomBlob(blob.boundingBox, blob.life + foodEnergy, randomValueChange(blob.velocity, modifyingPropertyRange), blob.degradationEffect, randomValueChange(blob.fieldOfViewRadius, modifyingPropertyRange), MovingStrategies.baseMovement)
    newBlob match {
      case b : BaseBlob => Set(
          BaseBlob(blob.boundingBox, blob.life + foodEnergy, blob.velocity, blob.degradationEffect, blob.fieldOfViewRadius, blob.movementStrategy),
          b)
      case _ => Set()
    }
  }

  def poisonousFoodEffect(blob: Blob): Set[SimulableEntity] = ???

  // used for static entities
  def neutralEffect(blob: Blob): Set[SimulableEntity] = {
    blob match {
      case b : BaseBlob => Set(b)
      case _ => Set()
    }
  }

  def mudEffect(blob: Blob): Set[SimulableEntity] = {
    val currentVelocity: Velocity = if (blob.velocity > 0) blob.velocity - 1 else blob.velocity
    Set(SlowBlob(
      blob.boundingBox, blob.life, currentVelocity, blob.degradationEffect, blob.fieldOfViewRadius, blob.movementStrategy, COOLDOWN_DEFAULT, blob.velocity))
  }


  private def randomBlob(boundingBox: Rectangle, life: Life, velocity: Velocity, degradationEffect: DegradationEffect[Blob], fieldOfViewRadius: Int, movementStrategy: MovementStrategy): Blob = {
    // TODO: cases for other blob types
    rand.nextInt(1 /* number of blob types*/) match {
      case 0 => BaseBlob(boundingBox, life, velocity, degradationEffect, fieldOfViewRadius, movementStrategy)
    }
  }

  /* min = value - range, max = value + range */
  private def randomValueChange(value: Int, range: Int): Int = {
    value + rand.nextInt(range + 1) - range
  }
}
