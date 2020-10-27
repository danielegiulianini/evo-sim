package evo_sim.dsl

object EntitiesCreation {
  /*This method enables syntax like this:
  World(
    ...world properties...
    15 of BaseBlob(...baseblob properties...)
  )*/

  implicit class FromIntToSet(int:Int) {
    def of[T](t: =>T) = Iterator.fill(int)(t).toList
  }
}
