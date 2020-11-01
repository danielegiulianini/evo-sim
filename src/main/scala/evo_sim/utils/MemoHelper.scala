package evo_sim.utils

/** Provides a utility method for "memoizing" values computed by a function and just retrieving them when invoked
 * more than once with the same input, instead of recomputing them every time from scratch.
 */
object MemoHelper {
  /** Returns a map that will cache the value computed before actually returning it.
   * In case of cache hit (i.e. when it is called with the same input more than once), the map returned will simply
   * return the cached value. If there is a cache miss, it will evaluate the expression passed, update the
   * cache with the new value and return that cached value.
   *
   * It should be used only if function is referentially transparent, the value returned by the function shall be
   * invalid otherwise.
   * @param f the [[Function1]] whose input-output mappings want to be memoized
   * @tparam I the type of the function parameter
   * @tparam O the type of the function return value
   * @return a map that will be able to cache input-output function mappings
   */
  def memoize[I, O](f: I => O): I => O = new collection.mutable.HashMap[I, O]() {
    override def apply(key: I): O = getOrElseUpdate(key, f(key))
  }
}
