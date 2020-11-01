package evo_sim.core

import evo_sim.utils.TupleUtils.{everyElementPairedWithOnlyOneOtherElement => multipleCollisionsRemoved}
import evo_sim.model.Entities.{BaseBlob, CannibalBlob, PoisonBlob, SlowBlob}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.{Blob, Food, Obstacle}
import evo_sim.model.Intersection.intersected
import evo_sim.model.{World}
import evo_sim.model.World.worldEnvironmentUpdated



object SimulationLogic {
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

  /**
   * Collision detection between 2 entities is carried out by noticing intersection between their
   * [[evo_sim.model.BoundingBox]]es.
   *
   * @param world the world
   * @return
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
