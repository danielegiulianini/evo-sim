package evo_sim.model

import evo_sim.model.EntityStructure.Blob
import evo_sim.model.EntityStructure.DomainImpl.Velocity

object TemperatureEffect {

  // TODO: normalizzare sul range della temperatura raggiungibile (?)
  def standardTemperatureEffect(blob: Blob, temperature: Int): Velocity =
    blob.velocity + (1 * Math.sin(2 * Math.PI * temperature / Constants.ITERATIONS_PER_DAY)).toInt

}
