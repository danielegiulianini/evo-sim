package evo_sim.core

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World
import evo_sim.model.World._
import evo_sim.view.View

import scala.concurrent.ExecutionContext

object SimulationEngine {

  private def immediateContext: ExecutionContext = new ExecutionContext {
    def execute(runnable: Runnable) {
      runnable.run()
    }

    def reportFailure(cause: Throwable): Unit = {
      cause.printStackTrace()
      System.exit(-1)
    }
  }

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
      world.entities //entitiesAfterCollision
    )
  }


  def started(): Unit = {
    View.GUIBuilt()
    View.inputReadFromUser().onComplete(e => {
      val environment = e.get
      val world = worldCreated(environment)
      View.simulationGUIBuilt()
      simulationLoop(world)
    })(immediateContext)

    def simulationLoop(world: World): Unit = {
      val updatedWorld = worldUpdated(world)
      val worldAfterCollisions = collisionsHandled(updatedWorld)
      View.rendered(worldAfterCollisions)
    }

  }
}
