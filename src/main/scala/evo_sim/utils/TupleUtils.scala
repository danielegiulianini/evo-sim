package evo_sim.utils

import evo_sim.utils.QueriableImplicits.ContainsForHomogeneousTuple2Set
import evo_sim.utils.QueriableImplicits.ContainsForSet.contained
import evo_sim.utils.Queriable.containedAnyOf
import evo_sim.utils.TupleUtils.Tuple2Types.HomogeneousTuple2Set



object TupleUtils {

  object Tuple2Types {
    type HomogeneousTuple2[A] = (A, A)
    type HomogeneousTuple2Set[A] = Set[(A, A)]
    object HomogeneousTuple2Set {
      def apply[A](): HomogeneousTuple2Set[A] = Set()
    }
  }

  def toTuple2[A](a: A): (A, A) = (a, a)

  //givenElementIntoOnlyOneTupleOrReversed
  //givenElementPairedWithOnlyOneOtherElement
  def everyElementPairedWithOnlyOneOtherElement[T](mySet: HomogeneousTuple2Set[T]): HomogeneousTuple2Set[T] =
    mySet.foldLeft(HomogeneousTuple2Set[T]())(
      (acc, t) =>
        if (contained(acc, t.swap) || !containedAnyOf(acc, t)) acc + t else acc)

}



/*
object TupleUtils {
  def toTuple2[A](a: A): (A, A) = (a, a)

  //givenElementIntoOnlyOneTupleOrReversed
  //givenElementPairedWithOnlyOneOtherElement
  def everyElementPairedWithOnlyOneOtherElement[T1](mySet: Set[(T1, T1)]): Set[(T1, T1)] =
    mySet.foldLeft(Set[(T1, T1)]())(
      (acc, t) =>
        if acc.contains(t.swap) || !containedAnyOf(acc, t)) acc + t else acc)

  def contained[T1](t: (T1, T1), element: T1): Boolean = t._1 == element || t._2 == element
  implicit class TupleCanContain[T](t: (T, T)) { //pimping DOT NOTATION
    def contained(elem: T): Boolean = TupleUtils.contained(t, elem)
  }

  def contained[T1](mySet: Set[(T1, T1)], elem: T1): Boolean =
    mySet.exists(_.contained(elem))

  def containedAnyOf[T1](mySet: Set[(T1, T1)], elem: (T1, T1)): Boolean =
    contained(mySet, elem._1) || contained(mySet, elem._2)


  /*def contained[T1](t: (T1, T1), element: T1): Boolean = t._1 == element || t._2 == element
  implicit class Tuple2CanContain[T](t: (T, T)) { //pimping DOT NOTATION
    def contained(elem: T): Boolean = TupleUtils.contained(t, elem)
  }

  def contained[T](mySet: Set[(T, T)], elem: T): Boolean = mySet.exists(_.contained(elem))
  implicit class SetCanContain[T](mySet:Set[(T, T)]) { //pimping DOT NOTATION
    def contained(elem: T): Boolean = TupleUtils.contained(mySet, elem)
  }
  */
  /*def containedAnyOf[T](mySet: Set[(T, T)], elem: (T, T)): Boolean = contained(mySet, elem._1) || contained(mySet, elem._2)
  implicit class SetCanContain2[T](t:Set[(T, T)]) { //pimping DOT NOTATION
    def containedAnyOf(elem: (T, T)): Boolean = TupleUtils.containedAnyOf(t, elem)
  }*/

}
*/

