package evo_sim.model.effects

import evo_sim.model.Constants
import evo_sim.model.Entities.PoisonBlob
import evo_sim.model.EntityStructure.DomainImpl.Life
import evo_sim.model.EntityStructure.Living

/**
 * This object contains the degradation effects that will be passed at the Living entities DegradationEffect property.
 * Foods will have simpler degradation effect while Blob will have some specific degradations based his status.
 */
object DegradationEffect{
  private val DOUBLE = 2
  /**
   * Standard degradation effect for [[evo_sim.model.EntityStructure.Living]] entities.
   * A degradation effect reduce the life of an entity during time.
   * @param entity the degradation effect will be applied to this entity.
   * @tparam A the entity is a subtype of [[evo_sim.model.EntityStructure.Living]].
   * @return the life with the new value decreased.
   */
  def standardDegradation[A <: Living](entity: A): Life = entity.life - DOUBLE * Constants.STANDARD_LIFE_DECREASE

  /**
   * Degradation effect that is applied when a blob is poisoned.
   * @param blob the degradation effect will be applied to this blob [[evo_sim.model.Entities.PoisonBlob]].
   * @return the life with the new value decreased.
   */
  def poisonBlobDegradation(blob: PoisonBlob) : Life = blob.life - DOUBLE * ( DOUBLE + Constants.STANDARD_LIFE_DECREASE)
}
