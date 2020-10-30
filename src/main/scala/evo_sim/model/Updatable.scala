package evo_sim.model

import evo_sim.model.EntityBehaviour.{Simulable, SimulableEntity}
import evo_sim.model.EntityStructure.Entity

/** Defines the ability of being updatable, i.e. how a type extending this trait reacts when [[SimulationLogic]]
 * calls updated on it, once per iteration.
 * Together with [[Entity]] and [[Collidable]] represents the only requirement for a type to be
 * part of the simulation.
 */
trait Updatable {
  /**
   * Specifies how an [[Entity]] behaves when it is notified an update event. It's invoked once per iteration.
   * It represents its evolution during the simulation depending on current World state.
   * @param world the World at the current iteration
   * @return a Set of [[SimulableEntity]]s that replaces this [[Entity]] at the next iteration. A set gives the
   *         chance to remove the entity from the simulation or generating an updated version of it or spawning new
   *         [[SimulableEntity]]s (like children)
   */
  def updated(world: World) : Set[SimulableEntity]
}

//companion object with some updatable implementations ready to be (re)used (in the future)
object Updatable {

  trait NeutralUpdatable extends Simulable {
    self : Entity with Collidable =>
    override def updated(world:World): Set[SimulableEntity] = Set(self)
  }
}