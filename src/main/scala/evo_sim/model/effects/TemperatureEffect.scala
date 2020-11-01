package evo_sim.model.effects

import evo_sim.model.Constants
import evo_sim.model.EntityStructure.DomainImpl.Velocity
import evo_sim.model.World.timeOfTheDay
import evo_sim.utils.MemoHelper.memoize
import evo_sim.utils.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidal

/** Provides functions to determine a temperature-dependent velocity variation */
object TemperatureEffect {

  /** Sinusoidal Sin algorithm, time of the day dependent.
   *
   * @return the velocity variation
   */
  def standardTemperatureEffect: ((Int, Int)) => Velocity = memoize({
    case (temperature: Int, currentIteration: Int) =>
        zeroPhasedZeroYTranslatedSinusoidal(Constants.VELOCITY_MODIFIER * temperature)(timeOfTheDay(currentIteration))
  })

}
