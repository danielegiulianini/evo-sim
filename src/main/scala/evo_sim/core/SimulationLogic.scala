package evo_sim.core

import evo_sim.utils.TupleUtils.{everyElementPairedWithOnlyOneOtherElement => multipleCollisionsRemoved}
import evo_sim.model.entities.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.EntityStructure.{Blob, Food, Obstacle}
import evo_sim.model.entities.entityStructure.Intersection.intersected
import evo_sim.model.world.World.worldEnvironmentUpdated
import evo_sim.model.entities.entityBehaviour.Collidable
import evo_sim.model.entities.entityStructure.BoundingBox
import evo_sim.model.world
import evo_sim.model.world.World


/** Contains utilities used for updating the world after each iteration and for detecting and resolving collisions
 * between entities.
 * */
object SimulationLogic {
  /**
   *  Updates the world by creating a new version of it with updated parameters that represent the effect of time
   *  elapsed from the previous iteration. It takes care of updating the [[SimulableEntities]] that populate it
   *  at the moment and the [[world.World.EnvironmentParameters]] like temperature and luminosity as well as
   *  saving the old World state for final historical data analysis.
   * @param world the world resulting by the previous iteration
   * @return a new version of the world
   */
  def worldUpdated(world: World): World = {
    val updatedEnvironmentParameters = worldEnvironmentUpdated(world)

    world.copy(
      temperature = updatedEnvironmentParameters.temperature,
      luminosity = updatedEnvironmentParameters.luminosity,
      currentIteration = world.currentIteration + 1,
      entities = world.entities.foldLeft(Set[SimulableEntity]())((updatedEntities, entity) => updatedEntities ++ entity.updated(world)),
      worldHistory = world #:: world.worldHistory
    )
  }

  /** Detects collisions between the [[SimulableEntity]] instances populating the [[World]] and gathers the
   * changes induced by the collisions on them to build the updated version of the [[World]].
   * Collision detection between 2 entities is carried out by noticing intersection between their
   * [[BoundingBox]]es.
   *
   * @param world the world whose entities' collisions are to be detected.
   * @return the world after collisions are resolved by invoking collided on [[Collidable]] component
   *         of [[SimulableEntity]] instances populating the world.
   */
  def collisionsHandled(world: World): World = {

    val collisions = multipleCollisionsRemoved(for {
      i <- world.entities
      j <- world.entities
      if i != j && intersected(i.boundingBox, j.boundingBox)
    } yield (i, j))

    def collidingEntities = collisions.map(_._1)

    def entitiesAfterCollision =
      collisions.foldLeft(world.entities -- collidingEntities)((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    var blobnormal = 0
    var cannibal = 0
    var blobpoison = 0
    var blobslow = 0
    var obstaclen = 0
    var foodsn = 0
    val a: List[SimulableEntity] = entitiesAfterCollision.toList
      a.sortWith(_.name < _.name).foreach(e => {
      println(e.name)
      if (e.isInstanceOf[Food]) foodsn = foodsn + 1
      else if (e.isInstanceOf[CannibalBlob]) cannibal = cannibal + 1
      else if (e.isInstanceOf[BaseBlob]) blobnormal = blobnormal + 1
      else if (e.isInstanceOf[Obstacle]) obstaclen = obstaclen + 1
      else if (e.isInstanceOf[SlowBlob]) blobslow = blobslow + 1
      else if (e.isInstanceOf[PoisonBlob]) blobpoison = blobpoison + 1
    })

    println("--------")
    println("BaseBlob: " + blobnormal)
    println("CannibalBlob: " + cannibal)
    println("PoisonBlob: " + blobpoison)
    println("SlowBlob: " + blobslow)
    println("Food: " + foodsn)
    println("Obstacle: " + obstaclen)

    world.copy(
      entities = entitiesAfterCollision,
    )

  }
}
