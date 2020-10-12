package evo_sim.core

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.Intersection.intersected
import evo_sim.model.World
import evo_sim.model.World._
import evo_sim.view.ViewModule

object SimulationEngine {

  def worldUpdated(world: World): World =
    World(
      world.width,
      world.height,
      world.currentIteration + 1,
      world.entities.foldLeft(world)((updatedWorld, entity) =>
        World(
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
      if i != j && intersected(i.boundingBox, i.boundingBox)
    } yield (i, j)

    def entitiesAfterCollision =
      collisions.foldLeft(Set.empty[SimulableEntity])((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    World(
      world.width,
      world.height,
      world.currentIteration,
      entitiesAfterCollision //world.entities
    )
  }

  def started(): Unit = {
    ViewModule.GUIBuilt()
    val environment = ViewModule.inputReadFromUser()
    val world = worldCreated(environment)
    ViewModule.simulationGUIBuilt()
    // ! val days = environment.daysNumber
    simulationLoop(world)

    def simulationLoop(world: World): Unit = {
      println("iteration: " + world.currentIteration + "/ " + world.totalIterations)

      val startingTime = System.currentTimeMillis();
      val updatedWorld = worldUpdated(world)
      val worldAfterCollisions = collisionsHandled(updatedWorld)
      ViewModule.rendered(worldAfterCollisions)

      val endingTime = System.currentTimeMillis()  //val endingTime = System.nanoTime();
      val elapsed = endingTime - startingTime

      waitUntil(elapsed, 1000)  //period in milliseconds

      def waitUntil(from: Long, period: Long): Unit = {
        if (from < period)
          try
            Thread.sleep((period - from))
          catch {
            case ignore: InterruptedException =>
          }
      }

      if (worldAfterCollisions.currentIteration < worldAfterCollisions.totalIterations)
        simulationLoop(worldAfterCollisions)
    }
  }
}
