package evo_sim.core

import scala.language.postfixOps
import cats.data.StateT
import cats.effect.IO
import evo_sim.utils.TupleUtils.toTuple2
import evo_sim.model.world.World.WorldHistory
import evo_sim.model.world.World
import evo_sim.view.swing.{SwingView => View}
//import evo_sim.view.cli.{CLIView => View}
import scala.concurrent.duration.FiniteDuration

object Simulation {

  /** Represents the IO monad for the Simulation*/
  type SimulationIO[A] = IO[A] //could be not generic: type SimulationIO = IO[Unit]

  /** Represents the [[StateT]] monad transformer that allows to stack [[cats.effect.IO]] monad with [[cats.data.State]] monad.
   * StateT allows to hide state passing by make it implicit inside StateT monad flatmap.*/
  type Simulation[A] = StateT[SimulationIO, World, A] //type Simulation = StateT[SimulationIO, World, Unit]

  /** Helper to create [[StateT]] monad from a [[SimulationIO]] monad */
  def liftIo[A](v: SimulationIO[A]): Simulation[A] = StateT[SimulationIO, World, A](s => v.map((s, _)))

  /** Helper to create StateT monad from a World => (World, A) function */
  def toStateT[A](f: World => (World, A)): Simulation[A] = StateT[IO, World, A](s => IO(f(s)))

  /** Helper to create StateT monad from a World => World function */
  def toStateTWorld(f: World => World): Simulation[World] = toStateT[World](w => toTuple2(f(w)))


  /** Provide conversions from [[cats.effect.IO]] instances and [[cats.data.State]] instances to
   * more general [[StateT]] monad so to allow them to compose (and to be used together in the same
   * for-comprehension).
   */
  object toStateTConversions {
    def worldUpdated(): Simulation[World] =
      toStateTWorld(SimulationLogic.worldUpdated)

    def collisionsHandled(): Simulation[World] =
      toStateTWorld(SimulationLogic.collisionsHandled)

    def worldRendered(worldAfterCollisions: World): Simulation[Unit] =
      liftIo(View.rendered(worldAfterCollisions))

    def resultShowed(worldHistory: WorldHistory): Simulation[Unit] =
      liftIo(View.resultsShowed(worldHistory))

    def getTime: Simulation[FiniteDuration] =
      liftIo(TimingIO.getTime)

    def waitUntil(from: FiniteDuration, to: FiniteDuration): Simulation[Unit] =
      liftIo(TimingIO.waitUntil(from, to))
  }
}










