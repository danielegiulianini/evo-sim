package evo_sim.model

import evo_sim.model.World.TrigonometricalOps.Sinusoidal.Curried.zeroPhasedZeroYTranslatedSinusoidalSin
import evo_sim.model.World.MemoHelper.memoize
import evo_sim.model.Utils.timeOfTheDay

/** Provides functions to determine a luminosity-dependent field of view range variation */
object LuminosityEffect {

  /**
   * Sinusoidal Sin algorithm, time of the day dependent.
   *
   * @return the field of view range variation
   */
  def standardLuminosityEffect: ((Int, Int)) => Int = memoize({
    case (luminosity, currentIteration) =>
      (Constants.FOW_MODIFIER * luminosity *
        zeroPhasedZeroYTranslatedSinusoidalSin(Constants.FOW_AMPLITUDE)(timeOfTheDay(currentIteration))).toInt
  })

}
