package evo_sim.core

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.Intersection.intersected
import evo_sim.model.World
import evo_sim.model.World._
import evo_sim.view.swing.View

object SimulationEngine {

  def worldUpdated(world: World): World = {
    val updatedEnvironmentParameters = worldEnvironmentUpdated(world)
    world.copy(
      temperature = updatedEnvironmentParameters.temperature,
      luminosity = updatedEnvironmentParameters.luminosity,
      currentIteration = world.currentIteration + 1,
      entities = world.entities.foldLeft(Set[SimulableEntity]())((updatedEntities, entity) => updatedEntities ++ entity.updated(world))
    )
  }


  def collisionsHandled(world: World): World = {
    def collisions = for {
      i <- world.entities
      j <- world.entities
      if i != j && intersected(i.boundingBox, j.boundingBox)
    } yield (i, j)

    def collidingEntities = collisions.map(_._1)

    def entitiesAfterCollision =
      collisions.foldLeft(world.entities -- collidingEntities)((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    World(
      world.temperature,
      world.luminosity,
      world.width,
      world.height,
      world.currentIteration,
      entitiesAfterCollision,
      world.totalIterations
    )
  }

  def started(): Unit = {
    View.inputViewBuiltAndShowed()
    val environment = View.inputReadFromUser()
    val world = worldCreated(environment)

    View.simulationViewBuiltAndShowed()
    /*val startingTime = System.currentTimeMillis()
    View.rendered(world)
    val endingTime = System.currentTimeMillis() //val endingTime = System.nanoTime();
    val elapsed = endingTime - startingTime
    //waitUntil(elapsed, 1000) //period in milliseconds*/
    simulationLoop(world)

    @scala.annotation.tailrec
    def simulationLoop(world: World): Unit = {
      val startingTime = System.currentTimeMillis()
      val updatedWorld = worldUpdated(world)
      val worldAfterCollisions = collisionsHandled(updatedWorld)
      View.rendered(worldAfterCollisions)

      val endingTime = System.currentTimeMillis() //val endingTime = System.nanoTime();
      val elapsed = endingTime - startingTime

      //waitUntil(elapsed, 1000) //period in milliseconds

      if (worldAfterCollisions.currentIteration < worldAfterCollisions.totalIterations)
        simulationLoop(worldAfterCollisions)
    }

    def waitUntil(from: Long, period: Long): Unit = {
      if (from < period)
        try
          Thread.sleep(period - from)
        catch {
          case _: InterruptedException =>
        }
    }
  }
}
