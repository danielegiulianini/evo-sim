package evo_sim.model

import evo_sim.model.EntityStructure.Blob
import evo_sim.model.EntityStructure.DomainImpl.Velocity

object TemperatureEffect {

  // TODO: normalizzare sul range della temperatura raggiungibile (?)
  def standardTemperatureEffect(currentIteration: Int): Velocity = {
    val deviation: Int = 3 //CONST -> si può calibrare
    val x: Int = (deviation * Math.sin(2.00 * (currentIteration / Constants.ITERATIONS_PER_DAY.toDouble * Math.PI))).toInt
    val lambda: Int = 2   //normalizzatore (volendo si possono usare altre tecniche di normalizzazione)
    val normalize: Int = 3 //-> valore della temperatura deciso dall'utente
    ((x / lambda) * deviation / normalize)
  }
}
