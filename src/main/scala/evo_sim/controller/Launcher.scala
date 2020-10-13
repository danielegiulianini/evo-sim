package evo_sim.controller

import evo_sim.core.SimulationEngine

object Launcher extends App {
  SimulationEngine.started().unsafeRunSync()
}
