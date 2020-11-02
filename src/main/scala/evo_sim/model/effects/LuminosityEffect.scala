package evo_sim.model.effects

import evo_sim.model.Constants
import evo_sim.model.World.timeOfTheDay
import evo_sim.utils.MemoHelper.memoize
import evo_sim.utils.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidal

/** Provides functions to determine a luminosity-dependent field of view range variation */
object LuminosityEffect {

  /**
   * Sinusoidal Sin algorithm, time of the day dependent.
   *
   * @return the field of view range variation
   */
  def standardLuminosityEffect: ((Int, Int)) => Int = memoize({
    case (luminosity, currentIteration) =>
        zeroPhasedZeroYTranslatedSinusoidal(Constants.FOV_MODIFIER * luminosity)(timeOfTheDay(currentIteration))
  })

}
