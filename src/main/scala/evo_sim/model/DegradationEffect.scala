package evo_sim.model

import evo_sim.model.Entities.PoisonBlob
import evo_sim.model.EntityStructure.DomainImpl.Life
import evo_sim.model.EntityStructure.{Blob, Food, Living}


/**
 * This object contains the degradation effects that will be passed at the Living entities DegradationEffect property
 * Foods will have simplier degradation effect while Blob will have some specific degradations based on their velocity, dimension, resistance ecc...
 */
object DegradationEffect{
  val STANDARD_LIFE_DECREASE = 1
  //extends the standard Degradation
  def standardDegradation[A <: Living](entity: A): Life = entity.life - 2 * STANDARD_LIFE_DECREASE
  //poison degradation effect
  def poisonBlobDegradation(blob: PoisonBlob) : Life = blob.life - 2 * ( 1 + STANDARD_LIFE_DECREASE)
}
