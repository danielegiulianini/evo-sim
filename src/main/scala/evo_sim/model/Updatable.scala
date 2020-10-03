package evo_sim.model

import evo_sim.model.Simulation.Simulable

trait Updatable {
  def updated(world: World) : Set[Simulable]
}


/**
* Degradation Effect -> apply effect
* Move
* */