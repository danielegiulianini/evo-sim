package evo_sim.core

import cats.effect.IO
import cats.effect.IO.unit
import evo_sim.core.Simulation.liftIo

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{DurationLong, FiniteDuration}

/** Contains some utilities for performing time operations in a purely functional fashion.
 */
object TimingOps {
  implicit val timer = IO.timer(ExecutionContext.global)

  /** Returns a [[IO]] description that when evaluated retrieves the current system time in milliseconds.
   */
  def getTime() =
    IO { System.currentTimeMillis().millis }


  def waitUntil(from: FiniteDuration, to: FiniteDuration) =
    if (from < to) {
      IO.sleep(to - from)
    } else unit

}
