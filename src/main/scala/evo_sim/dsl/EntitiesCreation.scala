package evo_sim.dsl

object EntitiesCreation {

  /**
   * Implicit class that enables rich syntax on Int instances.
   */
  implicit class FromIntToList(int:Int) {

    /**
     * This method enables a more declarative and concise syntax for a Set creation.
     * Using it in Entity creation results in this syntax:
     *
     * {{{
     * val baseBlobs = 15 of BaseBlob(
     * ... blob properties...
     * )
     * }}}
     * Instead of:
     * {{{
     * val baseBlobs = Iterator.tabulate(15)(i => BaseBlob(
     * ... blob properties...
     * )
     * }}}
     *
     * @param t the [[Function0]] used to fill the Set to be returned.
     * @tparam T the type of the elements added to the Set to be returned.
     * @return a Set of int elements of type T
     */
    def of[T](t: =>T): Set[T] = Iterator.fill(int)(t).toSet
  }
}