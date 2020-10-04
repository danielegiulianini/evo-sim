package evo_sim.core

import evo_sim.model.EntityBehaviour.Simulable
import evo_sim.model.World

import scala.language.postfixOps
import evo_sim.view.View
import evo_sim.model.World._


object SimulationEngine {

  def worldUpdated(world: World) : World =
    World(
      world.currentIteration + 1,
      world.entities.foldLeft(world)((updatedWorld, entity) =>
        World (
          world.currentIteration,
          entity.updated(updatedWorld)
        )
      ).entities
    )

 
}
