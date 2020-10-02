package evo_sim.model

import evo_sim.model.Simulation.Simulable

trait Updatable {
  def updated(world: World) : Set[Simulable]
}
