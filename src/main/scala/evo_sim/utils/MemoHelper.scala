package evo_sim.utils

/** Provides a utility method for "memoizing" values computed by a function and just retrieving them instead of
 * recomputing them every time from scratch.
 */
object MemoHelper {
  /**
   * If there is a cache hit, getOrElse simply returns the cached value.
   * If there is a cache miss, it evaluates the expression we pass to it, which basically calls isPrime
   * for the input, updates the cache with the primality of the input and returns the cached value.
   *
   * Returns a map where corresponding to the application of the
   * It should be used only if function is referentially transparent.
   * @param f
   * @tparam I the type
   * @tparam O
   * @return the [[PartialFunction]]
   */
  def memoize[I, O](f: I => O): PartialFunction[I, O] = new collection.mutable.HashMap[I, O]() {
    override def apply(key: I): O = getOrElseUpdate(key, f(key))
  }
}
