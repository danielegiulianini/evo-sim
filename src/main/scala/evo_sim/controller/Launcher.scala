package evo_sim.controller

import evo_sim.core.SimulationEngine
import evo_sim.dsl.SimulationStart._

/** The application's entry point */
object Launcher extends App {
  SimulationEngine.started run()
}