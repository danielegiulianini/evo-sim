package evo_sim.utils


trait Queriable[F[_]]{
  def contained[T](t: F[T], elem: T) : Boolean
}

object Queriable {

  def containedAnyOf[F[_]: Queriable, A](t: F[A], elem: (A, A)): Boolean =
    implicitly[Queriable[F]].contained(t, elem._1) || implicitly[Queriable[F]].contained(t, elem._2)

  //enabling DOT notation
  /*implicit class ContainablePimped[F[_]: Queriable, T](qa: F[T]) {
    def contained[T](elem: T): Boolean =
      implicitly[Queriable[F]].contained(qa, elem)
  }*/
}

object ContainableImplicits {
  implicit object ContainsForSet extends Queriable[Set]{
    override def contained[T](t: Set[T], elem: T): Boolean = t.contains(elem)
  }
   implicit object ContainsForHomogeneousTuple2 extends Queriable[({type HomogeneousTuple2[A] = (A, A)})#HomogeneousTuple2]{
     override def contained[T](t: (T, T), elem: T): Boolean =
       t._1 == elem || t._2 == elem
   }
   implicit object ContainsForHomogeneousTuple2Set extends Queriable[({type HomogeneousTuple2Set[A] = Set[(A, A)]})#HomogeneousTuple2Set]{
     override def contained[T](t: Set[(T, T)], elem: T): Boolean = {
       //t.exists(evo_sim.utils.ContainableImplicits.ContainsForHomogeneousTuple2.contained(_, elem)) //t.exists(_.contained(elem))
       t.exists(ContainableImplicits.ContainsForHomogeneousTuple2.contained(_, elem)) //t.exists(_.contained(elem))
     }
   }
 }

