package evo_sim.model

import evo_sim.model.DomainImpl.Life

/**
 * This object contains the degradation effects that will be passed at the Living entities DegradationEffect property
 * Foods will have simplier degradation effect while Blob will have some specific degradations based on their velocity, dimension, resistance ecc...
 */
object DegenerationEffect{
  //standard degradation effect
  def standardDegeneration(entity: Entity): Life = 1
  //blob specific degradation effect
  def baseDegradation(blob: Blob) : Life = blob.velocity + 1
}
