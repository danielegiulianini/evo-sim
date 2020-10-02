package evo_sim.model

import evo_sim.model.Simulation.Simulable

trait Collidable {
  def collided(other: Simulable) : Set[Simulable]
}

