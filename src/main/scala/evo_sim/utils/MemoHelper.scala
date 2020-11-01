package evo_sim.utils

/** Provides an optimization which stores the results of an expensive function call and returns the cached result when
 * the same inputs occur again
 */
object MemoHelper {
  def memoize[I, O](f: I => O): I => O = new collection.mutable.HashMap[I, O]() {
    override def apply(key: I): O = getOrElseUpdate(key, f(key))
  }
}
