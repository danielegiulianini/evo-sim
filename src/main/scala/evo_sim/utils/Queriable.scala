package evo_sim.utils

/** A type class that represents the ability for a type functor to be queried, i.e. asked whether it contains
 * a given element.
 *
 * @tparam F the type functor to be enriched with the [[Queriable]]#contained function
 */
trait Queriable[F[_]]{
  /**
   *
   * @param t the type functor instance to be pimped with the contained functionality
   * @param elem the element to be searched inside the t instance
   * @tparam T the generic type of the element to be searched
   * @return whether the type functor instance contains the element provided
   */
  def contained[T](t: F[T], elem: T) : Boolean
}

/**
 * Provides some algorithms built on top of [[Queriable]] ready to be used once a given type functor is given
 * a conversion to it. See [[QueriableImplicits]] for implicit conversions to [[Queriable]] type class.
 */
object Queriable {

  /**
   *
   * @param t
   * @param elem
   * @tparam F
   * @tparam A
   * @return
   */
  def containedAnyOf[F[_]: Queriable, A](t: F[A], elem: (A, A)): Boolean =
    implicitly[Queriable[F]].contained(t, elem._1) || implicitly[Queriable[F]].contained(t, elem._2)

  //enabling DOT notation
  /*implicit class ContainablePimped[F[_]: Queriable, T](qa: F[T]) {
    def contained[T](elem: T): Boolean =
      implicitly[Queriable[F]].contained(qa, elem)
  }*/
}

/** Provides some implicit conversions for [[evo_sim.utils.Queriable]] type class.
 */
object QueriableImplicits {
  implicit object ContainsForSet extends Queriable[Set]{
    override def contained[T](t: Set[T], elem: T): Boolean = t.contains(elem)
  }

   implicit object ContainsForHomogeneousTuple2 extends Queriable[({type HomogeneousTuple2[A] = (A, A)})#HomogeneousTuple2]{
     override def contained[T](t: (T, T), elem: T): Boolean =
       t._1 == elem || t._2 == elem
   }

   implicit object ContainsForHomogeneousTuple2Set extends Queriable[({type HomogeneousTuple2Set[A] = Set[(A, A)]})#HomogeneousTuple2Set]{
     override def contained[T](t: Set[(T, T)], elem: T): Boolean = {
       t.exists(QueriableImplicits.ContainsForHomogeneousTuple2.contained(_, elem))
     }
   }
 }

