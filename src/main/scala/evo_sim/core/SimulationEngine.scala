package evo_sim.core

import scala.language.postfixOps
import cats.effect.IO
import evo_sim.core.Simulation.toStateTConversions._
import evo_sim.view.swing.{SwingView => View}
//import evo_sim.view.cli.{CLIView => View}
import evo_sim.core.Simulation._
import evo_sim.model.World

import scala.concurrent.duration._

/** The engine of the simulation.
 *
 */
object SimulationEngine {

  /**
   *  Provides a lazy description of the actions required to start the simulation.
   */
  def started(): IO[Unit] = {
    for {
      //_ <- inputViewBuiltAndShowed()
      env <- View.inputReadFromUser() //env <- fromFuture(IO(ViewModule.inputReadFromUser())) //if using promise  //implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
      _ <- simulationLoop().runS(World(env))
    } yield ()
  }

  /** Provides a lazy description of the cyclic behaviour of the simulation as a sequence of flatmap applications.
   *
   */
  def simulationLoop() : Simulation[Unit] = for {
    startTime <- getTime
    _ <- worldUpdated
    worldAfterCollisions <- collisionsHandled
    _ <- worldRendered(worldAfterCollisions)
    currentTime <- getTime
    _ <- waitUntil(currentTime - startTime, 10 millis)  //TODO use constants
    - <- if (worldAfterCollisions.currentIteration < worldAfterCollisions.totalIterations)
      simulationLoop() else resultShowed(worldAfterCollisions.worldHistory)
  } yield ()
}









