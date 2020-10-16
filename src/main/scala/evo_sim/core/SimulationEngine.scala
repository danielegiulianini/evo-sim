package evo_sim.core


import cats.effect.{ContextShift, IO}
//import cats.effect.IO.{fromFuture, unit}
import evo_sim.core.Simulation.{Simulation, liftIo, toStateTWorld}
import evo_sim.model.World
import scala.concurrent.duration._
import evo_sim.core.Logging._
import evo_sim.core.TimingOps.{getTime, waitUntil}
import evo_sim.view.swing.View //import evo_sim.view.cli.View

object SimulationEngine {

  //maybe move these conversions from here to SimulationLogic or Simulation...
  def worldUpdated(): Simulation[World] = toStateTWorld {
    SimulationLogic.worldUpdated
  }

  def collisionsHandled(): Simulation[World] = toStateTWorld {
    SimulationLogic.collisionsHandled
  }

  //missing guiBuilt, resultGuiBuiltAndShowed as IO-monads

  def worldRendered(worldAfterCollisions: World) =
    liftIo(IO { View.rendered(worldAfterCollisions) })

  def inputReadFromUser() =
    IO { View.inputReadFromUser() }




  def started() = {
    for {
      - <- IO {
        log("building gui")
        View.inputViewBuiltAndShowed()
      }
      env <- inputReadFromUser()        //env <- fromFuture(IO(ViewModule.inputReadFromUser())) //if using promise  //implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
      /*- <- IO {
        log("building simulation gui")
        SwingGUI.simulationViewBuiltAndShowed()
      }*/
      _ <- IO {
        log("calling sim loop")
        (for {
          _ <- IO {View.simulationViewBuiltAndShowed()}
          _ <- simulationLoop().runS(World(env))
        } yield()).unsafeRunSync()
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
        - <- IO { View.resultViewBuiltAndShowed(worldAfterCollisions) }
      } yield ())
  } yield ()
}




//to remove after debugging complete
object Logging {
  def log(message: String) = println(Thread.currentThread.getName+": " + message)
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