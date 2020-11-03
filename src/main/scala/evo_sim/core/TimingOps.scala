package evo_sim.core

import cats.effect.{IO, Timer}
import cats.effect.IO.unit

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{DurationLong, FiniteDuration}

/** Contains some utilities for performing time operations in a purely functional fashion.
 */
object TimingOps {
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  /** Returns a [[IO]] description that when evaluated retrieves the current system time in milliseconds.
   */
  def getTime: IO[FiniteDuration] =
    IO { System.currentTimeMillis().millis }

  /** Returns a [[IO]] description that when evaluated will create an asynchronous task that will wait
   * for the interval between specified durations.
   *
   * @param from the lower bound for of the time interval to be slept
   * @param to the upper bound of the time interval until when sleep.
   * @return the IO monad describing the sleeping operation.
   */
  def waitUntil(from: FiniteDuration, to: FiniteDuration): IO[Unit] =
    if (from < to) {
      IO.sleep(to - from)
    } else unit

}
