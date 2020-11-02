package evo_sim.model.entities.entityBehaviour

import EntityBehaviour.{Simulable, SimulableEntity}
import evo_sim.model.entities.entityStructure.EntityStructure.Entity

/** Defines how a type extending this trait reacts when it collides with another [[SimulableEntity]], i.e. how
 * it state changes.
 * Together with [[Updatable]] (they forms [[Simulable]]) and [[Entity]], it represents the only requirement for a
 * type to be part of the simulation.
 */
trait Collidable {
  /**
   * Describes the effect of collision on this Collidable implementation.
   * @param other the SimulableEntity which this Collidable collides with.
   * @return a Set of [[SimulableEntity]]s that replaces this [[Entity]] at the next iteration. A set gives the
   *                  chance to remove the entity from the simulation or generating an updated version of it or spawning new
   *                  [[SimulableEntity]]s (like children)
   */
  def collided(other: SimulableEntity) : Set[SimulableEntity]
}

/** This companion object of [[Collidable]] provides some Collidable ready-to-be-(re)used
 * implementations that can be stacked on a given [[Updatable]] instance independently of its actual
 * implementation.
 * By leveraging them, implementing Updatable suffices for a [[Entity]] to be part of the simulation.
 * To be noted that Updatable c.o. provides some ready implementations of Collidable too.
 */
object Collidable {

  /**
   * Defines a [[Collidable]] implementation that does nothing on collision notification. It returns the
   * [[SimulableEntity]] as it would have been if the collision would not have taken place, without any
   * modification.
   * Given an [[Entity]] and a [[Updatable]] implementation, it's ready to be used for fulfilling the requirements
   * needed to take part  in the simulation.
   */
  trait NeutralCollidable extends Simulable {
    self: Entity with Updatable =>
    override def collided(other: SimulableEntity): Set[SimulableEntity] = Set(self)
  }
}