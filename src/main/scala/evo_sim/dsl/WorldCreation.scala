package evo_sim.dsl

object WorldCreation {
  implicit class IntWithDegrees(temperatureInCelsius:Int){
    lazy val fromCelsiusToFarenheit = 9/5 +32 //should rethink in terms of function
    def °<() = temperatureInCelsius
    def F() = temperatureInCelsius * fromCelsiusToFarenheit
  }

  implicit class IntWithMeters(lengthInMeters: Int){
    def m(): Int = lengthInMeters
    def km() = 10 * hm
    def hm(): Int = 100 * lengthInMeters
  }


}
