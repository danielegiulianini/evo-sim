package evo_sim.model

package u13.typeclasses

import evo_sim.model.Entities.BaseBlob

//type class for reproduction
trait Reproducible[A] {
  def reproduce(b1: A, b2: A): Set[A]
}

object Reproducible {
  def reproduce[A: Reproducible](b1: A, b2: A): Set[A] =
    implicitly[Reproducible[A]].reproduce(b1, b2)

  //dot notation
  implicit class ReproducibleWithDotNotation[A:Reproducible](b:A) {
    def reproducedWith(b2:A) = Reproducible.reproduce(b,b2)
  }
}

object PlusImplicits {

  implicit object BaseBlobReproduction extends Reproducible[BaseBlob]{
    override def reproduce(a1: BaseBlob, a2: BaseBlob) =
/*REPRODUCTION LOGIC GOES HERE*/
      Set()
  }
}


