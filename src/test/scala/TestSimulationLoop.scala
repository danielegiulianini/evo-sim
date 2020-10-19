import evo_sim.core.SimulationEngine.simulationLoop
import evo_sim.model.{Environment, World}
import evo_sim.core.Simulation._


class TestSimulationLoop {

  val initialEnvironment : Environment = Environment(temperature = 2,
    luminosity = 3,
    initialBlobNumber= 10,
    initialFoodNumber= 3,
    initialObstacleNumber= 3,
    daysNumber=5)


  val w = simulationLoop().runS(World(initialEnvironment)).unsafeRunSync()
  w.currentIteration //should be equal to daysNumber * iterationPerDay
}
