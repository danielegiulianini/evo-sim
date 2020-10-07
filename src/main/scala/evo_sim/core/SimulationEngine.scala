package evo_sim.core

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.{Environment, World}
import evo_sim.model.World._
import evo_sim.view.View._

//import scala.concurrent.{Future}

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
      collisions.foldLeft(Set.empty[SimulableEntity])((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

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


  //stub for function of view still to be implemented
  /*def inputReadFromUser(): Future[Environment] = {
    Future[Environment]()
  }*/


  def simulationLoop(world:World) : Unit = {
    val updatedWorld = worldUpdated(world)
    val worldAfterCollisions = collisionsHandled(updatedWorld)
    //View.rendered(worldAfterCollisions)
  }

}
