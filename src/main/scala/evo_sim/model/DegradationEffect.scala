package evo_sim.model

import evo_sim.model.EntityStructure.DomainImpl.Life
import evo_sim.model.EntityStructure.{Blob, Entity, Food}


/**
 * This object contains the degradation effects that will be passed at the Living entities DegradationEffect property
 * Foods will have simplier degradation effect while Blob will have some specific degradations based on their velocity, dimension, resistance ecc...
 */
object DegradationEffect{
  //standard degradation effect
  def standardDegradation(entity: Entity): Life = 1
  //extends the standard Degradation
  def foodDegradation(entity: Food): Life = standardDegradation(entity)
  //blob specific degradation effect, velocity + standardDegradation
  def baseBlobDegradation(blob: Blob) : Life = blob.velocity + standardDegradation(blob)
}
