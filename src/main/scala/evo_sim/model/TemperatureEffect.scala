package evo_sim.model

import evo_sim.model.EntityStructure.DomainImpl.Velocity
import evo_sim.model.World.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidalSin

/** Provides functions to determine a temperature-dependent velocity variation */
object TemperatureEffect {

  /** Sinusoidal Sin algorithm
   *
   * @param temperature the temperature value to use
   * @return the velocity variation
   */
  def standardTemperatureEffect(temperature: Int): Velocity = {
    val x = temperature.toFloat / Constants.TEMPERATURE_MAX_DELTA.toFloat
    zeroPhasedZeroYTranslatedSinusoidalSin(Constants.VELOCITY_AMPLITUDE)(x)
  }

}
