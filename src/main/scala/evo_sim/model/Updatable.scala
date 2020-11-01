package evo_sim.model

import evo_sim.model.EntityBehaviour.{Simulable, SimulableEntity}
import evo_sim.model.EntityStructure.Entity

/** Defines the ability of being updatable, i.e. how a type extending this trait reacts when [[evo_sim.core.SimulationLogic]]
 * calls updated on it, once per iteration.
 * Together with [[Collidable]] (they forms [[Simulable]]) and [[Entity]], it represents the only requirement for a
 * type to be part of the simulation.
 */
trait Updatable {
  /**
   * Specifies how an [[Entity]] behaves in response to an update notification, which happens once
   * per iteration.
   * It represents its evolution during the simulation depending on current World state.
   * @param world the World at the current iteration of the simulation
   * @return a Set of [[SimulableEntity]]s that replaces this [[Entity]] at the next iteration. A set gives the
   *         chance to remove the entity from the simulation or generating an updated version of it or spawning new
   *         [[SimulableEntity]]s (like children)
   */
  def updated(world: World) : Set[SimulableEntity]
}

/** This companion object of [[Updatable]] provides some interchangeable Updatable ready-to-be-(re)used
 * implementations that can be mounted on a given [[Collidable]] instance independently of its actual
 * implementation.
 * By leveraging them, implementing Collidable suffices for a [[Entity]] to be part of the simulation.
 * To be noted that Collidable c.o. provides some ready implementations of Collidable too.
 */
object Updatable {

  /**
   * Defines an [[Updatable]] implementation that does nothing on update notification. It returns the
   * [[SimulableEntity]] as it was at the previous iteration, without any modification. Given an Entity
   * and a Collidable implementation, it's ready to be used for fulfilling the requirements needed to take part
   * in the simulation.
   */
  trait NeutralUpdatable extends Simulable {
    self : Entity with Collidable =>
    /**
     * returns the [[Entity]] with [[Collidable]] without any changes wrt the previous iteration.
     * @param world the World at the current iteration of the simulation
     *  @return a Set of [[SimulableEntity]]s that replaces this [[Entity]] at the next iteration. A set gives the
     *         chance to remove the entity from the simulation or generating an updated version of it or spawning new
     *         SimulableEntitys (like children)
     */
    override def updated(world:World): Set[SimulableEntity] = Set(self)
  }
}