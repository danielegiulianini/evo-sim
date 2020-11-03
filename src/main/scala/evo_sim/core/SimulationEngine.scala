package evo_sim.core

import scala.language.postfixOps
import cats.effect.IO
import evo_sim.core.Simulation.toStateTConversions._
import evo_sim.model.world.Constants.SIMULATION_LOOP_PERIOD
import evo_sim.model.world.{Constants, World}
import evo_sim.view.swing.{SwingView => View}
//import evo_sim.view.cli.{CLIView => View}
import evo_sim.core.Simulation._

import scala.concurrent.duration._

/** The engine of the simulation, responsible for setting up the simulation, updating at a constant rate
 * the [[World]], detecting and handle collisions, displaying the World updated and showing historical
 * data gathered by the simulation.
 */
object SimulationEngine {

  /**
   *  Provides a monadic description of the actions required to start the simulation.
   *  The simulation does not start until invoking "run" on the [[IO]] returned from this function.
   */
  def started(): IO[Unit] = {
    for {
      env <- View.inputReadFromUser()
      _ <- simulationLoop().runS(World(env))
    } yield ()
  }

  /** Provides a lazy description of the cyclic behaviour of the simulation as a sequence of unit and flatmap
   * applications.
   * Every simulation iteration is made up of ad update step, followed by a collisions-detection-and-handling phase
   * and by a display step, that finally renders the resulting World on the view.
   * World is updated at a constant rate defined by [[Constants.SIMULATION_LOOP_PERIOD]] constant.
   * At the end of simulation a statistical view of the data gathered by the simulation is displayed.
   * This method doesn't actually run the simulation until performing "run" on the [[IO]] returned.
   */
  def simulationLoop() : Simulation[Unit] = for {
    startTime <- getTime
    _ <- worldUpdated
    worldAfterCollisions <- collisionsHandled
    _ <- worldRendered(worldAfterCollisions)
    currentTime <- getTime
    _ <- waitUntil(currentTime - startTime, SIMULATION_LOOP_PERIOD millis)
    _ <- if (worldAfterCollisions.currentIteration < worldAfterCollisions.totalIterations)
      simulationLoop() else resultShowed(worldAfterCollisions.worldHistory)
  } yield ()
}









