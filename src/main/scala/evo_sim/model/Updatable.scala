package evo_sim.model

import evo_sim.model.EntityBehaviour.Simulable

trait Updatable {
  def updated(world: World) : Set[EntityBehaviour.Simulable]
}


/**
* Degradation Effect -> apply effect
* Move
* */