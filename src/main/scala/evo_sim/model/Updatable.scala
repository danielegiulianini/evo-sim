package evo_sim.model

import evo_sim.model.Simulable.Simulable

trait Updatable {
  def updated(world: World) : Set[Simulable]
}
