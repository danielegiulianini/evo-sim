package evo_sim.core

import cats.effect.{ContextShift, IO}
import evo_sim.core.Simulation.toIoConversions.inputReadFromUser
import evo_sim.core.Simulation.toStateTConversions._
import evo_sim.core.TimingOps.{getTime, waitUntil}
//import cats.effect.IO.{fromFuture, unit}
import evo_sim.core.Simulation._//{Simulation, liftIo, toStateTWorld}
import evo_sim.model.World
import scala.concurrent.duration._
import evo_sim.core.Logging._
import evo_sim.core.TimingOps.{getTime, waitUntil}
import evo_sim.view.swing.View //import evo_sim.view.cli.View

object SimulationEngine {

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









