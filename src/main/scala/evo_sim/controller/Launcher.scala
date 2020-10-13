package evo_sim.controller

import cats.effect.{ContextShift, IO}
import evo_sim.core.SimulationEngine


object Launcher extends App {
  SimulationEngine.started().unsafeRunSync()
}

