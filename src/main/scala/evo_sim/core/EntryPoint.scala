package evo_sim.core



object EntryPoint extends App {
  SimulationEngine.started().unsafeRunSync()
}
