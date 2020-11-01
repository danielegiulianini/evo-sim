package evo_sim.dsl

import evo_sim.core.Simulation.SimulationIO

object SimulationStart {

  /** Enables prettier method name than "unsafeRunAsync" for starting simulation.*/
  implicit class SimulationCanStart[A](simulation: SimulationIO[A]) {
    def run(): A = simulation unsafeRunSync
  }
}
