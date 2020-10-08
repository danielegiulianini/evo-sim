package evo_sim.model

import evo_sim.model.Entities.BaseBlob
import evo_sim.model.EntityBehaviour.SimulableEntity

object Effect {

  // adds 10 to blob life
  def standardFoodEffect(blob: BaseBlob): Set[SimulableEntity] = Set(BaseBlob(blob.boundingBox, blob.life + 10, blob.velocity, blob.degradationEffect, blob.fieldOfViewRadius, blob.movementStrategy))

  def poisonousFoodEffect(blob: BaseBlob): Set[SimulableEntity] = ???

  // used for static entities
  def neutralEffect(blob: BaseBlob): Set[SimulableEntity] = Set(blob)
}
