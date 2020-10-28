package evo_sim.model

import evo_sim.model.EntityStructure.DomainImpl.Velocity
import evo_sim.model.World.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidalSin
import evo_sim.model.World.MemoHelper.memoize
import evo_sim.model.Utils.timeOfTheDay

/** Provides functions to determine a temperature-dependent velocity variation */
object TemperatureEffect {

  /** Sinusoidal Sin algorithm, time of the day dependent.
   *
   * @return the velocity variation
   */
  def standardTemperatureEffect: ((Int, Int)) => Velocity = memoize({
    case (temperature: Int, currentIteration: Int) =>
        zeroPhasedZeroYTranslatedSinusoidalSin(Constants.VELOCITY_MODIFIER * temperature)(timeOfTheDay(currentIteration))
  })

}
