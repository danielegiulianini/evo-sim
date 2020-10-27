package evo_sim.dsl

object WorldCreation {
  implicit class IntWithDegrees(int:Int){
    def °<() = 10*int
  }
}
