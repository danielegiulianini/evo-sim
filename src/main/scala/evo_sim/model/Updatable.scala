package evo_sim.model

import evo_sim.model.BoundingBox.Circle
import evo_sim.model.Entities.{BaseBlob, CannibalBlob}
import evo_sim.model.EntityBehaviour.{Simulable, SimulableEntity}
import evo_sim.model.EntityStructure.{Blob, Entity, Food, Living}

trait Updatable {
  def updated(world: World) : Set[SimulableEntity]
}

//companion object with some updatable implementations ready to be (re)used (in the future)
object Updatable {

  trait NeutralUpdatable extends Simulable {
    self : Entity with Collidable =>
    override def updated(world:World): Set[SimulableEntity] = Set(self)
  }
}