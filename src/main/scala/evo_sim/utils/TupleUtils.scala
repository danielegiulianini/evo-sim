package evo_sim.utils

import evo_sim.utils.QueriableImplicits.ContainsForHomogeneousTuple2Set
import evo_sim.utils.QueriableImplicits.ContainsForSet.contained
import evo_sim.utils.Queriable.containedAnyOf
import evo_sim.utils.TupleUtils.Tuple2Types.HomogeneousTuple2Set



object TupleUtils {

  /**
   *
   */
  object Tuple2Types {
    type HomogeneousTuple2[A] = (A, A)
    type HomogeneousTuple2Set[A] = Set[(A, A)]
    object HomogeneousTuple2Set {
      def apply[A](): HomogeneousTuple2Set[A] = Set()
    }
  }

  /** Encapsulates the given element into a [[Tuple2]] instance.
   * @param a an element to convert to a tuple
   * @return a [[Tuple2]] instance containing two a elements.
   */
  def toTuple2[A](a: A): (A, A) = (a, a)


  def everyElementPairedWithOnlyOneOtherElement[T](mySet: HomogeneousTuple2Set[T]): HomogeneousTuple2Set[T] =
    mySet.foldLeft(HomogeneousTuple2Set[T]())(
      (acc, t) =>
        if (contained(acc, t.swap) || !containedAnyOf(acc, t)) acc + t else acc)

}