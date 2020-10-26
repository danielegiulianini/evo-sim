package evo_sim.model

import evo_sim.model.World.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidalSin

/** Provides functions to determine a luminosity-dependent field of view range variation
 *
 */
object LuminosityEffect {

  /** Sinusoidal Sin algorithm
   *
   * @param luminosity the luminosity value to use
   * @return the field of view range variation
   */
  def standardLuminosityEffect(luminosity: Int): Int = {
    val x = luminosity.toFloat / Constants.LUMINOSITY_MAX_DELTA.toFloat
    zeroPhasedZeroYTranslatedSinusoidalSin(Constants.FOW_AMPLITUDE)(x)
  }

}
