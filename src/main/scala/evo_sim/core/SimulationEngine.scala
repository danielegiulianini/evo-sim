package evo_sim.core

import cats.data.StateT
import cats.effect.{ContextShift, IO}
import cats.effect.IO.{fromFuture, unit}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.Intersection.intersected
import evo_sim.model.World

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import evo_sim.core.SimulationEngine.Logging._
import evo_sim.core.SimulationEngine.SimulationLogic.DayPhase.DayPhase
import evo_sim.view.swing.SwingGUI

object SimulationEngine {

  type SimulationIO[A] = IO[A] //could be not generic: type SimulationIO = IO[Unit]
  type Simulation[A] = StateT[SimulationIO, World, A] //type Simulation = StateT[SimulationIO, World, Unit]

  //helper to create StateT monad from a IO monad
  def liftIo[A](v: SimulationIO[A]): Simulation[A] = StateT[SimulationIO, World, A](s => v.map((s, _)))

  def toStateT[A](f: World => (World, A)): Simulation[A] = StateT[IO, World, A](s => IO(f(s)))

  //helper to create StateT monad from a World to World function
  def toStateTWorld(f: World => World): Simulation[World] = toStateT[World](w => toTuple(f(w)))

  def toTuple[A](a: A) = (a, a)

  def worldUpdated(): Simulation[World] = toStateTWorld {
    SimulationLogic.worldUpdated
  }

  def collisionsHandled(): Simulation[World] = toStateTWorld {
    SimulationLogic.collisionsHandled
  }

  implicit val timer = IO.timer(ExecutionContext.global)

  def getTime() = liftIo(IO { System.currentTimeMillis().millis })   //def getTime() = liftIo(IO( (w: World) => (w, System.currentTimeMillis().millis)) ) //as a statet monad returns a identical new world but x seconds older

  def waitUntil(from: FiniteDuration, period: FiniteDuration) =
    liftIo(if (from < period) {
      IO.sleep(period - from)
    } else unit)

  //missing guiBuilt as IO-monad

  def worldRendered(worldAfterCollisions: World) =
    liftIo(IO { SwingGUI.rendered(worldAfterCollisions) })

  def inputReadFromUser() =
    IO { SwingGUI.inputReadFromUser() }


  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def started() = {
    for {
      - <- IO {
        log("building gui")
        SwingGUI.inputViewBuiltAndShowed()
      }
      env <- inputReadFromUser()        //env <- fromFuture(IO(ViewModule.inputReadFromUser())) //if using promise
      /*- <- IO {
        log("building simulation gui")
        SwingGUI.simulationViewBuiltAndShowed()
      }*/
      _ <- IO {
        log("calling sim loop")
        (for {
          _ <- IO {SwingGUI.simulationViewBuiltAndShowed()}
          _ <- simulationLoop().runS(World(env))
        } yield()).unsafeRunSync()
        /*
        * ViewModule.simulationGUIBuilt()
        * simulationLoop().runS()
        * */
        /*simulationLoop().runS(World(env))*/
      }
    } yield ()
  }

  def simulationLoop() : Simulation[Unit] = for {
    _ <- toStateTWorld { (w: World) => {
      log("it " + w.currentIteration + " / " + w.totalIterations)
      w
    }}
    startTime <- getTime
    _ <- worldUpdated
    worldAfterCollisions <- collisionsHandled
    _ <- worldRendered(worldAfterCollisions)
    currentTime <- getTime
    _ <- waitUntil(currentTime - startTime, 10 millis)
    - <- if (worldAfterCollisions.currentIteration < worldAfterCollisions.totalIterations)
      simulationLoop() else
      liftIo( for {
        _ <- IO { log("simulation ended, printing sim statistics") }
        - <- IO { SwingGUI.resultViewBuiltAndShowed(worldAfterCollisions) }
      } yield ())//liftIo(IO(unit))
  } yield ()



  //maybe to move outside this object
  object SimulationLogic {
    /*def worldUpdated(world: World): World =
      World(
        world.width,
        world.height,
        world.currentIteration + 1,
        world.entities.foldLeft(Set[SimulableEntity]())((updatedEntities, entity) => updatedEntities ++ entity.updated(world)),
        world.totalIterations
      )*/

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
  }

  //to remove after debugging complete
  object Logging {
    def log(message: String) = println(Thread.currentThread.getName+": " + message)
  }
}










/*
object SimulationEngine {

def worldUpdated(world: World): World = {
World(
world.width,
world.height,
world.currentIteration + 1,
world.entities.foldLeft(Set[SimulableEntity]())((updatedEntities, entity) => updatedEntities ++ entity.updated(world)),
world.totalIterations
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
world.width,
world.height,
world.currentIteration,
world.entities ++ entitiesAfterCollision,
world.totalIterations
)
}

def started(): Unit = {
ViewModule.GUIBuilt()
val environment = ViewModule.inputReadFromUser()
val world = worldCreated(environment)
ViewModule.simulationGUIBuilt()
val startingTime = System.currentTimeMillis()
ViewModule.rendered(world)
val endingTime = System.currentTimeMillis() //val endingTime = System.nanoTime();
val elapsed = endingTime - startingTime
waitUntil(elapsed, 1000) //period in milliseconds
simulationLoop(world)

@scala.annotation.tailrec
def simulationLoop(world: World): Unit = {
println("iteration: " + world.currentIteration + "/ " + world.totalIterations)
val startingTime = System.currentTimeMillis()
val updatedWorld = worldUpdated(world)
val worldAfterCollisions = collisionsHandled(updatedWorld)
ViewModule.rendered(worldAfterCollisions)

val endingTime = System.currentTimeMillis() //val endingTime = System.nanoTime();
val elapsed = endingTime - startingTime

waitUntil(elapsed, 1000) //period in milliseconds

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
*/