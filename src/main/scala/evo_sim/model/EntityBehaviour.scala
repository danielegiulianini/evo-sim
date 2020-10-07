package evo_sim.model

import evo_sim.model.EntityStructure.{Blob, Entity}
import evo_sim.model.Entities.BaseBlob

object EntityBehaviour {
  type Simulable = Updatable with Collidable
  type SimulableEntity = Entity with Simulable //trait Simulable

  //stub for blob (does nothing)
  trait BlobBehaviour extends Simulable {
    self: Blob =>

    override def updated(world: World): Set[SimulableEntity] = {
      print("baseBlob");
      Set(this)
    }

    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case blob: BaseBlob => Set(this)
      case _ => Set(this)
    }
  }

  //other entities go here:

}
