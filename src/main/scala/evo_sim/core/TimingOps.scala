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

  /** Returns a [[IO]] description that when evaluated will create an asynchronous task that will sleep
   * for the the interval between specified durations.
   *
   * @param from the lower bound for of the time interval to be slept
   * @param to the upper bound of the time interval until when sleep.
   * @return the IO monad describing the sleeping operation.
   */
  def waitUntil(from: FiniteDuration, to: FiniteDuration) =
    if (from < to) {
      IO.sleep(to - from)
    } else unit

}
