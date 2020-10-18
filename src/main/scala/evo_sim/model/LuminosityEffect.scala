package evo_sim.model

import evo_sim.model.EntityStructure.Blob

object LuminosityEffect {

  // TODO: normalizzare sul range della luminosità raggiungibile (?)
  def standardLuminosityEffect(blob: Blob, luminosity: Int): Int =
    blob.fieldOfViewRadius + (1 * Math.sin(2 * Math.PI * luminosity / Constants.ITERATIONS_PER_DAY)).toInt

}
