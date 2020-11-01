package evo_sim.utils

import evo_sim.utils.QueriableImplicits.ContainsForHomogeneousTuple2Set
import evo_sim.utils.QueriableImplicits.ContainsForSet.contained
import evo_sim.utils.Queriable.containedAnyOf
import evo_sim.utils.TupleUtils.Tuple2Types.HomogeneousTuple2Set



object TupleUtils {

  /** An object containing some [[Tuple2]]-related types aliases. */
  object Tuple2Types {

    /** An homogeneous [[Tuple2]], i.e. a Tuple2 with 2 elements of the same generic type.
     * @tparam A the type of the element contained inside the the Tuple2.
     */
    type HomogeneousTuple2[A] = (A, A)

    /** A [[Set]] of [[HomogeneousTuple2]]s.
     * @tparam A
     */
    type HomogeneousTuple2Set[A] = Set[(A, A)]

    /** A companion object that acts as a factory for HomogeneousTuple2Sets.
     */
    object HomogeneousTuple2Set {
      /**
       * Build an empty HomogeneousTuple2Set instance of the specified type.
       * @tparam A the generic type of HomogeneousTuple2Set to build
       */
      def apply[A](): HomogeneousTuple2Set[A] = Set()
    }
  }

  /** Encapsulates the given element into a [[Tuple2]] instance.
   * @param a an element to convert to a tuple
   * @return a [[Tuple2]] instance containing two a elements.
   */
  def toTuple2[A](a: A): (A, A) = (a, a)

  /**
   * @param mySet
   * @tparam T
   * @return
   */
  def everyElementPairedWithOnlyOneOtherElement[T](mySet: HomogeneousTuple2Set[T]): HomogeneousTuple2Set[T] =
    mySet.foldLeft(HomogeneousTuple2Set[T]())(
      (acc, t) =>
        if (contained(acc, t.swap) || !containedAnyOf(acc, t)) acc + t else acc)

}