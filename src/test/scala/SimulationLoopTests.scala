import evo_sim.core.SimulationEngine.simulationLoop
import evo_sim.model.{Environment, World}
import evo_sim.core.Simulation._
import org.scalatest.FunSpec


class SimulationLoopTests extends App {

  val initialEnvironment : Environment = Environment(
    temperature = 2,
    luminosity = 3,
    initialBlobNumber = 10,
    initialPlantNumber = 3,
    initialObstacleNumber = 3,
    daysNumber = 5)

  val initialWorld = World(initialEnvironment)

  //test 1: check correct iteration count progress
  val w = simulationLoop().runS(initialWorld).run()
  w.currentIteration //should be equal to daysNumber * iterationPerDay

  //test 2: check for constant frame rate of simulationLoop

}
