package evo_sim.model

import evo_sim.model.EntityBehaviour.{SimulableEntity}

trait Collidable {
  def collided(other: SimulableEntity) : Set[SimulableEntity]
}

/**
 * collided(blob, blob)
 * collided(blob, food)
 * collided(blob, obstacle)
 * -> note, every collided with the specific type of entity, this permits to define the effect occurred to the blob
 *
  def collided(other: Blob) : Set[Simulable] = {
    val set = Set(other)
    set -> this cant be done since Blob does not extend Simulable (so does not extends Collidable and Updatable either)
  }
 */