package evo_sim.model

import evo_sim.model.EntityStructure.DomainImpl.Life
import evo_sim.model.EntityStructure.{Blob, Food}


/**
 * This object contains the degradation effects that will be passed at the Living entities DegradationEffect property
 * Foods will have simplier degradation effect while Blob will have some specific degradations based on their velocity, dimension, resistance ecc...
 */
object DegradationEffect{
  //standard degradation effect
  def standardDegradation(blob: Blob): Life = blob.life - 1
  //extends the standard Degradation
  def foodDegradation(food: Food): Life = food.life - 1
  //blob specific degradation effect, velocity + standardDegradation
  def baseBlobDegradation(blob: Blob) : Life = blob. life - (/*blob.velocity*/ 1 + standardDegradation(blob)) //calibrate parameter
  //poison degradation effect
  def posionBlobDegradation(blob: Blob) : Life = blob. life - 2*(/*blob.velocity*/ 1 + standardDegradation(blob)) //calibrate parameter
}
