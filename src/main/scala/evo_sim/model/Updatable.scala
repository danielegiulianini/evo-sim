package evo_sim.model

import evo_sim.model.EntityBehaviour.{SimulableEntity}

trait Updatable {
  def updated(world: World) : Set[SimulableEntity]
}


/**
* Degradation Effect -> apply effect
* Move
* */