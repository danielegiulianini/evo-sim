package evo_sim.core

import scala.language.postfixOps
import cats.effect.IO
import evo_sim.core.Simulation.toStateTConversions._
import evo_sim.view.swing.View  //import evo_sim.view.cli.View
import evo_sim.core.Logging._
import evo_sim.core.Simulation._
import evo_sim.core.TimingOps.{getTime, waitUntil}
import evo_sim.model.World

import scala.concurrent.duration._

object SimulationEngine {

  def started(): IO[Unit] = {
    for {
      //_ <- inputViewBuiltAndShowed()
      env <- View.inputReadFromUser()        //env <- fromFuture(IO(ViewModule.inputReadFromUser())) //if using promise  //implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
      _ <- simulationLoop().runS(World(env))
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
        - <- View.resultViewBuiltAndShowed(worldAfterCollisions)
      } yield ())
  } yield ()
}

//to remove after debugging complete
object Logging {
  def log(message: String) = println(Thread.currentThread.getName+": " + message)
}









