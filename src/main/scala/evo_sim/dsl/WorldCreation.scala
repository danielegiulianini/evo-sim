package evo_sim.dsl

object WorldCreation {
  implicit class IntWithDegrees(temperature:Int){
    def °<() = temperature
  }
}
