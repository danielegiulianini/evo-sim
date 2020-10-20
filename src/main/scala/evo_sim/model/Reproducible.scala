package evo_sim.model

import evo_sim.model.Entities.BaseBlob
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.Reproducible.reproduce

//type class for reproduction
trait Reproducible[A] {
  def reproduce(b1: A, b2: A): A
}

object Reproducible {
  def reproduce[A: Reproducible](b1: A, b2: A): A =
    implicitly[Reproducible[A]].reproduce(b1, b2)

  //dot notation
  implicit class ReproducibleWithDotNotation[A:Reproducible](b:A) {
    def reproducedWith(b2:A) = Reproducible.reproduce(b,b2)
  }
}

object ReproducibleImplicits {

  implicit object BaseBlobReproduction extends Reproducible[BaseBlob]{
    override def reproduce(a1: BaseBlob, a2: BaseBlob) =
/*REPRODUCTION LOGIC GOES HERE*/
      a1
  }
}

object ReproducibleExample {
  import ReproducibleImplicits._
  def collided(other: SimulableEntity): Set[SimulableEntity] = {
    other match {
      case _ : BaseBlob => Set(reproduce(other.asInstanceOf[BaseBlob], other.asInstanceOf[BaseBlob]))
    }
  }
}


