package evo_sim.model

import evo_sim.model.EntityStructure.Blob

object LuminosityEffect {

  // TODO: normalizzare sul range della luminosità raggiungibile (?)
  def standardLuminosityEffect(blob: Blob, luminosity: Int): Int =
    blob.fieldOfViewRadius + (1 * Math.sin(2 * Math.PI * luminosity / Constants.ITERATIONS_PER_DAY)).toInt

  /**
   * def f(i: Int): Integer =  {
   * val deviation: Int = 2 //CONST -> si può calibrare
   * val x: Int = (deviation * Math.sin(2.00 * (i / Constants.ITERATIONS_PER_DAY * Math.PI))).toInt
   * val lambda: Int = 1   //normalizzatore (volendo si possono usare altre tecniche di normalizzazione)
   * return (x / lambda) * deviation
   * }
   */

}
