package evo_sim.controller

import evo_sim.core.SimulationEngine
import evo_sim.dsl.SimulationStart._

object Launcher extends App {
  SimulationEngine.started run()
}