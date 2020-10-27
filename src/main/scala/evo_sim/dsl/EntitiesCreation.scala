package evo_sim.dsl

object EntitiesCreation {
  /*This method enables syntax like this:
  World(
    ...properties...
    15 of BaseBlob(life = 3,...)
  )*/

  implicit class FromIntToSet(int:Int) {
    def of[T](t: =>T) = Iterator.fill(int)(t).toList
  }
}
