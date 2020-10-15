package evo_sim.core

import cats.data.StateT
import cats.effect.IO
import evo_sim.core.Simulation.TupleUtils.toTuple
import evo_sim.model.World
import evo_sim.model.World


object Simulation {
  type SimulationIO[A] = IO[A] //could be not generic: type SimulationIO = IO[Unit]
  type Simulation[A] = StateT[SimulationIO, World, A] //type Simulation = StateT[SimulationIO, World, Unit]

  //helper to create StateT monad from a IO monad
  def liftIo[A](v: SimulationIO[A]): Simulation[A] = StateT[SimulationIO, World, A](s => v.map((s, _)))

  def toStateT[A](f: World => (World, A)): Simulation[A] = StateT[IO, World, A](s => IO(f(s)))

  //helper to create StateT monad from a World to World function
  def toStateTWorld(f: World => World): Simulation[World] = toStateT[World](w => toTuple(f(w)))

  implicit class SimulationCanStart[A](game: SimulationIO[A]) {
    def started() = game.unsafeRunSync()
  }

  object TupleUtils {
    def toTuple[A](a: A) = (a, a)
  }
}
