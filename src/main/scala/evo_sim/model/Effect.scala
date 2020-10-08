package evo_sim.model

import java.util.Random

import evo_sim.model.BoundingBox.Rectangle
import evo_sim.model.Entities.BaseBlob
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.DomainImpl.{DegradationEffect, Life, MovementStrategy, Rivals, Velocity}
import evo_sim.model.EntityStructure.{Blob, Intelligent}

object Effect {

  private val rand = new java.util.Random()
  private val modifyingPropertyRange = 5
  private val foodEnergy = 10

  // adds 10 to blob life
  def standardFoodEffect(blob: BaseBlob): Set[SimulableEntity] = {
    val newBlob = randomBlob(blob.boundingBox, blob.life + foodEnergy, randomValueChange(blob.velocity, modifyingPropertyRange), blob.degradationEffect, randomValueChange(blob.fieldOfViewRadius, modifyingPropertyRange), MovingStrategies.baseMovement)
    newBlob match {
      case b : BaseBlob => Set(
          BaseBlob(blob.boundingBox, blob.life + foodEnergy, blob.velocity, blob.degradationEffect, blob.fieldOfViewRadius, blob.movementStrategy),
          b)
      case _ => Set(blob)
    }
  }

  def poisonousFoodEffect(blob: BaseBlob): Set[SimulableEntity] = ???

  // used for static entities
  def neutralEffect(blob: BaseBlob): Set[SimulableEntity] = Set(blob)


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
