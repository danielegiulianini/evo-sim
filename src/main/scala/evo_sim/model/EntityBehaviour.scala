package evo_sim.model

import evo_sim.model.EntityStructure.Entity
import evo_sim.model.Entities.BaseBlob

object EntityBehaviour {
  //trait Simulable
  type SimulableEntity = Entity with Updatable with Collidable

  //stub for blob(does nothing)
  trait BlobBehaviour extends Updatable with Collidable {
    self: BaseBlob =>
    override def updated(world: World): Set[SimulableEntity] = {print("baseBlob");Set(this)}
    override def collided(other: SimulableEntity): Set[SimulableEntity] = other match {
      case blob: BaseBlob => Set(this)
      case _ => Set(this)
    }
  }

  //other entities go here
}
