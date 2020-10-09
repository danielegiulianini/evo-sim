package evo_sim.controller

import evo_sim.core.SimulationEngine

trait Simulation {
  def launch(): Unit = SimulationEngine.started()
}