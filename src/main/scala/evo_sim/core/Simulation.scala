package evo_sim.core

import cats.data.StateT
import cats.effect.IO
import evo_sim.core.TupleUtils.toTuple2
import evo_sim.model.{Environment, World}
import evo_sim.view.swing.View


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
    def run(): A = simulation.unsafeRunSync()
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
    def worldRendered(worldAfterCollisions: World): Simulation[Unit] =
      liftIo(View.rendered(worldAfterCollisions))
  }

}

object TupleUtils {
  def toTuple2[A](a: A): (A, A) = (a, a)

  //givenElementIntoOnlyOneTupleOrReversed
  //givenElementPairedWithOnlyOneOtherElement
  def everyElementPairedWithOnlyOneOtherElement[T1](mySet: Set[(T1, T1)]): Set[(T1, T1)] =
    mySet.foldLeft(Set[(T1, T1)]())(
      (acc, t) =>
        if (acc.contains(t.swap) || !containedAnyOf(acc, t)) acc + t else acc)


  trait Containable[F[_]]{
    def contained[A, T](t: F[A], elem: T) : Boolean
  }
  object Containable {
    implicit class ContainablePimped[F[_]: Containable, A] (ca: F[A]) {
      //enabling DOT notation
      def contained[T](elem: T): Boolean = implicitly[Containable[F]].contained(ca, elem)
    }
  }

  def contained[T1](t: (T1, T1), element: T1): Boolean = t._1 == element || t._2 == element
  implicit class Tuple2CanContain[T](t: (T, T)) { //pimping DOT NOTATION
    def contained(elem: T): Boolean = TupleUtils.contained(t, elem)
  }

  def contained[T](mySet: Set[(T, T)], elem: T): Boolean = mySet.exists(_.contained(elem))
  implicit class SetCanContain[T](mySet:Set[(T, T)]) { //pimping DOT NOTATION
    def contained(elem: T): Boolean = TupleUtils.contained(mySet, elem)
  }

  def containedAnyOf[T](mySet: Set[(T, T)], elem: (T, T)): Boolean = contained(mySet, elem._1) || contained(mySet, elem._2)
  implicit class SetCanContain2[T](t:Set[(T, T)]) { //pimping DOT NOTATION
    def containedAnyOf(elem: (T, T)): Boolean = TupleUtils.containedAnyOf(t, elem)
  }
}
