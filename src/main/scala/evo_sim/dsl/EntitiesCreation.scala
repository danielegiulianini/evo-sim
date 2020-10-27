package evo_sim.dsl

object EntitiesCreation {


  implicit class FromIntToList(int:Int) {
    def of[T](t: =>T) = Iterator.fill(int)(t).toSet
  }
}
