package evo_sim.model.world.effects

import evo_sim.model.entities.entityStructure.EntityStructure.Domain.Velocity
import evo_sim.model.world.Constants
import evo_sim.model.world.World.timeOfTheDay
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
