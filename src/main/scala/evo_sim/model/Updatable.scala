package evo_sim.model

import evo_sim.model.EntityBehaviour.SimulableEntity

trait Updatable {
  def updated(world: World) : Set[SimulableEntity]
}

object Updatable {
  trait FastAging extends Updatable {
    self : Updatable =>

    override def updated(world: World): Set[SimulableEntity] = {
      val b = updated(world)
      b.foreach(x => x.updated(world))
      b
    }
  }
}


/**
* Degradation Effect -> apply effect
* Move
* */