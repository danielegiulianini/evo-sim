package evo_sim.core

import cats.data.StateT
import cats.effect.IO
import evo_sim.core.TupleUtils.toTuple
import evo_sim.model.World
import evo_sim.view.swing.View


object Simulation {
  type SimulationIO[A] = IO[A] //could be not generic: type SimulationIO = IO[Unit]
  type Simulation[A] = StateT[SimulationIO, World, A] //type Simulation = StateT[SimulationIO, World, Unit]

  //helper to create StateT monad from a IO monad
  def liftIo[A](v: SimulationIO[A]): Simulation[A] = StateT[SimulationIO, World, A](s => v.map((s, _)))

  //helper to create StateT monad from a World to (World, A) function
  def toStateT[A](f: World => (World, A)): Simulation[A] = StateT[IO, World, A](s => IO(f(s)))

  //helper to create StateT monad from a World to World function
  def toStateTWorld(f: World => World): Simulation[World] = toStateT[World](w => toTuple(f(w)))

  //prettier method name than "unsafeRunAsync for starting simulation"
  implicit class SimulationCanStart[A](simulation: SimulationIO[A]) {
    def run() = simulation.unsafeRunSync()
  }


  //maybe move these conversions from here to 2. SimulationEngine...
  object toStateTConversions {
    def worldUpdated(): Simulation[World] = toStateTWorld {
      SimulationLogic.worldUpdated
    }
    def collisionsHandled(): Simulation[World] = toStateTWorld {
      SimulationLogic.collisionsHandled
    }
    //missing guiBuilt, resultGuiBuiltAndShowed as IO-monads
    def worldRendered(worldAfterCollisions: World) =
      liftIo(IO {
        View.rendered(worldAfterCollisions)
      })
    def inputReadFromUser() =
      IO {
        View.inputReadFromUser()
      }
  }

}




object TupleUtils {
  def toTuple[A](a: A) = (a, a)
}
