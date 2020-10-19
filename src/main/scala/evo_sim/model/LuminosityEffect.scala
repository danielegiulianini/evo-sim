package evo_sim.model

import evo_sim.model.EntityStructure.Blob

object LuminosityEffect {

  // TODO: normalizzare sul range della luminosità raggiungibile (?)
  def standardLuminosityEffect(currentIteration: Int): Int ={
    val deviation: Int = 3 //CONST -> si può calibrare
    val x: Int = (deviation * Math.sin(2.00 * (currentIteration / Constants.ITERATIONS_PER_DAY.toDouble * Math.PI))).toInt
    val lambda: Int = 2   //normalizzatore (volendo si possono usare altre tecniche di normalizzazione)
    val normalize: Int = 2 //-> valore della temperatura deciso dall'utente
    ((x / lambda) * deviation / normalize)
  }

}
