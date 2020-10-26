package evo_sim.model

import evo_sim.model.Entities.BaseBlob
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.Reproducible.reproduce
import evo_sim.model.Utils._

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
    override def reproduce(b1: BaseBlob, b2: BaseBlob) =
      b1.copy(
        name = b1.name + "-son" + nextValue,
        velocity = randomValueChange((b1.velocity + b2.velocity) / 2, Constants.DEF_MOD_PROP_RANGE),
        fieldOfViewRadius = randomValueChange((b1.fieldOfViewRadius + b2.fieldOfViewRadius) / 2, Constants.DEF_MOD_PROP_RANGE)
      )
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


