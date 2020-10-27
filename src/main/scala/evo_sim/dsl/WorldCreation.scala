package evo_sim.dsl

object WorldCreation {
  implicit class IntWithDegrees(temperature:Int){
    def °<() = temperature
  }

  implicit class IntWithMeters(length: Int){
    def km() = 1000*length
  }
}
