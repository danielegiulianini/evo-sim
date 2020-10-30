package evo_sim.core

import cats.data.StateT
import cats.effect.IO
import evo_sim.utils.TupleUtils.toTuple2
import evo_sim.model.World
import evo_sim.model.World.WorldHistory
import evo_sim.view.swing.{SwingView => View}


object Simulation {
  type SimulationIO[A] = IO[A] //could be not generic: type SimulationIO = IO[Unit]
  type Simulation[A] = StateT[SimulationIO, World, A] //type Simulation = StateT[SimulationIO, World, Unit]

  //helper to create StateT monad from a IO monad
  def liftIo[A](v: SimulationIO[A]): Simulation[A] = StateT[SimulationIO, World, A](s => v.map((s, _)))

  //helper to create StateT monad from a World to (World, A) function
  def toStateT[A](f: World => (World, A)): Simulation[A] = StateT[IO, World, A](s => IO(f(s)))

  //helper to create StateT monad from a World to World function
  def toStateTWorld(f: World => World): Simulation[World] = toStateT[World](w => toTuple2(f(w)))

  //prettier method name than "unsafeRunAsync for starting simulation"
  implicit class SimulationCanStart[A](simulation: SimulationIO[A]) {
    def run(): A = simulation unsafeRunSync
  }


  object toStateTConversions {
    def worldUpdated(): Simulation[World] =
      toStateTWorld { SimulationLogic.worldUpdated }

    def collisionsHandled(): Simulation[World] =
      toStateTWorld {SimulationLogic.collisionsHandled}

    def worldRendered(worldAfterCollisions: World): Simulation[Unit] =
      liftIo(View.rendered(worldAfterCollisions))

    def resultShowed(worldHistory: WorldHistory) =
      liftIo(View.resultViewBuiltAndShowed(worldHistory))
  }
}










