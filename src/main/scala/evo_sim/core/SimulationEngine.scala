package evo_sim.core

import evo_sim.controller.HandlerExecutionContext
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World
import evo_sim.model.World._

object SimulationEngine {

  def worldUpdated(world: World): World =
    World(
      world.width,
      world.height,
      world.currentIteration + 1,
      world.entities.foldLeft(world)((updatedWorld, entity) =>
        World (
          world.width,
          world.height,
          world.currentIteration,
          entity.updated(updatedWorld)
        )
      ).entities
    )

  def collisionsHandled(world: World): World = {
    def collisions = for {
      i <- world.entities
      j <- world.entities
      if i != j // && i.intersected(j.shape)//intersects(j.shape)
    } yield (i, j)
    
    def entitiesAfterCollision =
      collisions.foldLeft(Set.empty[SimulableEntity])((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    World(
      world.width,
      world.height,
      world.currentIteration,
      entitiesAfterCollision//world.entities
    )
  }

  def started(): Unit = {
    ViewModule.GUIBuilt()
    ViewModule.inputReadFromUser().onComplete(e => {
      val environment = e.get
      val world = worldCreated(environment)
      ViewModule.simulationGUIBuilt()
      simulationLoop(world)
    })(HandlerExecutionContext.immediateContext)

    def simulationLoop(world: World): Unit = {
      val updatedWorld = worldUpdated(world)
      val worldAfterCollisions = collisionsHandled(updatedWorld)
      ViewModule.rendered(worldAfterCollisions)
    }
  }
}
