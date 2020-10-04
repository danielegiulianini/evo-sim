package evo_sim.core

import evo_sim.model.EntityBehaviour.Simulable
import evo_sim.model.World

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

  def collisionsHandled(world:World) : World = {
    def collisions = for {
      i <- world.entities
      j <- world.entities
      if i != j // && i.intersected(j.shape)//intersects(j.shape)
    } yield (i, j)

    def entitiesAfterCollision =
      collisions.foldLeft(Set.empty[Simulable])((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    World(
      world.currentIteration,
      world.entities//entitiesAfterCollision
    )
  }



  def started() = {
    val environment = ??? //inputReadFromUser()
    val world = worldCreated(environment)
    simulationLoop(world)
  }


  def simulationLoop(world:World) : Unit = {
    val updatedWorld = worldUpdated(world)
    val worldAfterCollisions = collisionsHandled(updatedWorld)
    //View.rendered(worldAfterCollisions)
  }


}
