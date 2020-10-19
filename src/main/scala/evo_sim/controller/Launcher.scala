package evo_sim.controller

import evo_sim.core.SimulationEngine
import evo_sim.core.Simulation._
import evo_sim.core.SimulationEngine.simulationLoop
import evo_sim.model.{Environment, World}


object Launcher extends App {
  SimulationEngine.started run
}

