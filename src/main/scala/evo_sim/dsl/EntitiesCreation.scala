package evo_sim.dsl

object EntitiesCreation {
  /*This method enables syntax like this:

val baseBlobs = 15 of BaseBlob(
... blob properties...
)

    instead of:

val baseBlobs = Iterator.tabulate(15)(i => BaseBlob(
... blob properties...
)
  )*/

  implicit class FromIntToList(int:Int) {
    def of[T](t: =>T) = Iterator.fill(int)(t).toSet
  }
}
