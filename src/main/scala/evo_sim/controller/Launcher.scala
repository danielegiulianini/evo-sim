package evo_sim.controller

import cats.effect.{ContextShift, IO}
import evo_sim.controller.Logging.log
import evo_sim.core.SimulationEngine

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{DurationInt, FiniteDuration}

object Launcher extends App {
  SimulationEngine.started().unsafeRunSync()
}

object Logging {
  def log(message: String) = println(Thread.currentThread.getName+": " + message)
}
