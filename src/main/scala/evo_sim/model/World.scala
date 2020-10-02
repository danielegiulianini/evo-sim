package evo_sim.model

import evo_sim.model.Simulation.Simulable


object Simulation {
  type Simulable = Entity with Updatable with Collidable
}


case class World(currentIteration: Int, entities: Set[_<:Simulable])




