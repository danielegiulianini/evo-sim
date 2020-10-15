package evo_sim.view.cli

import evo_sim.model.{Environment, World}
import evo_sim.view.View

object View extends View {
  override def inputViewBuiltAndShowed(): Unit = println("Ready")

  override def inputReadFromUser(): Environment = {
    print("#Blobs: ")
    val blobs = scala.io.StdIn.readInt()
    print("#Foods: ")
    val foods = scala.io.StdIn.readInt()
    print("#Obstacles: ")
    val obstacles = scala.io.StdIn.readInt()
    print("Luminosity (cd): ")
    val luminosity = scala.io.StdIn.readInt()
    print("Temperature (°C): ")
    val temperature = scala.io.StdIn.readInt()
    print("#Days: ")
    val days = scala.io.StdIn.readInt()
    Environment(
      temperature = temperature,
      luminosity = luminosity,
      initialBlobNumber = blobs,
      initialFoodNumber = foods,
      initialObstacleNumber = obstacles,
      daysNumber = days
    )
  }

  override def simulationViewBuiltAndShowed(): Unit = println("Simulation started")

  override def rendered(world: World): Unit = {
    println("Iteration " + world.currentIteration + " of " + world.totalIterations)
    println("Temperature: " + world.temperature)
    println("Luminosity: " + world.luminosity)
    // TODO print real-time indicators
  }

  override def resultViewBuiltAndShowed(world: World): Unit = {
    // TODO: print final indicators
  }
}
