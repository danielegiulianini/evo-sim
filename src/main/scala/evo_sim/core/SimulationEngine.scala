package evo_sim.core

import evo_sim.core.SimulationEngine.DayPhase.DayPhase
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.Intersection.intersected
import evo_sim.model.World
import evo_sim.model.World._
import evo_sim.view.swing.SwingGUI

object SimulationEngine {

  object DayPhase extends Enumeration {
    type DayPhase = Value
    val Morning, Afternoon, Evening, Night = Value
  }

  def worldUpdated(world: World): World = {

    // TODO: iterationsPerDay solo una volta nel codice (c'è anche in world)
    val iterationsPerDay: Int = 100
    val phaseDuration: Int = iterationsPerDay / DayPhase.values.size

    def asDayPhase(iteration: Int): DayPhase = iteration % iterationsPerDay match {
      case i if phaseDuration >= i => DayPhase.Night
      case i if phaseDuration + 1 to phaseDuration * 2 contains i => DayPhase.Morning
      case i if phaseDuration * 2 + 1 to phaseDuration * 3 contains i => DayPhase.Afternoon
      case i if phaseDuration * 3 < i => DayPhase.Evening
    }

    val currentDayPhase = asDayPhase(world.currentIteration)
    val nextDayPhase = asDayPhase(world.currentIteration + 1)

    case class EnvironmentModifiers(temperature: Int, luminosity: Int)
    def environmentModifiers: EnvironmentModifiers = (currentDayPhase != nextDayPhase, nextDayPhase) match {
      case (true, DayPhase.Night) => EnvironmentModifiers(-7, -15)
      case (true, DayPhase.Morning) => EnvironmentModifiers(+10, +25)
      case (true, DayPhase.Afternoon) => EnvironmentModifiers(+7, +15)
      case (true, DayPhase.Night) => EnvironmentModifiers(-10, -25)
      case _ => EnvironmentModifiers(0, 0)
    }

    world.copy(
      temperature = world.temperature + environmentModifiers.temperature,
      luminosity = world.luminosity + environmentModifiers.luminosity,
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

    def entitiesAfterCollision =
      collisions.foldLeft(Set.empty[SimulableEntity])((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    World(
      world.temperature,
      world.luminosity,
      world.width,
      world.height,
      world.currentIteration,
      world.entities ++ entitiesAfterCollision,
      world.totalIterations
    )
  }

  def started(): Unit = {
    SwingGUI.inputViewBuiltAndShowed()
    val environment = SwingGUI.inputReadFromUser()
    val world = worldCreated(environment)
    SwingGUI.simulationViewBuiltAndShowed()
    val startingTime = System.currentTimeMillis()
    SwingGUI.rendered(world)
    val endingTime = System.currentTimeMillis() //val endingTime = System.nanoTime();
    val elapsed = endingTime - startingTime
    //waitUntil(elapsed, 1000) //period in milliseconds
    simulationLoop(world)

    @scala.annotation.tailrec
    def simulationLoop(world: World): Unit = {
      val startingTime = System.currentTimeMillis()
      val updatedWorld = worldUpdated(world)
      val worldAfterCollisions = collisionsHandled(updatedWorld)
      SwingGUI.rendered(worldAfterCollisions)

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
