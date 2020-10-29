package evo_sim.view.cli

import cats.effect.IO
import evo_sim.model.World.WorldHistory
import evo_sim.model.{Environment, World}
import evo_sim.view.View

object View extends View {
  override def inputReadFromUser(): IO[Environment] = for {
    _ <- IO apply print("#Blobs: ")
    blobs <- IO apply scala.io.StdIn.readInt()
    _ <- IO apply print("#Plants: ")
    foods <- IO apply scala.io.StdIn.readInt()
    _ <- IO apply print("#Obstacles: ")
    obstacles <- IO apply scala.io.StdIn.readInt()
    _ <- IO apply print("Luminosity (cd): ")
    luminosity <- IO apply scala.io.StdIn.readInt()
    _ <- IO apply print("Temperature (°C): ")
    temperature <- IO apply scala.io.StdIn.readInt()
    _ <- IO apply print("#Days: ")
    days = scala.io.StdIn.readInt()
    environment <- IO pure Environment(
      temperature = temperature,
      luminosity = luminosity,
      initialBlobNumber = blobs,
      initialPlantNumber = foods,
      initialObstacleNumber = obstacles,
      daysNumber = days
    )
  } yield environment

  override def rendered(world: World): IO[Unit] = for {
    _ <- IO apply println("Iteration " + world.currentIteration + " of " + world.totalIterations)
    _ <- IO apply println("Temperature: " + world.temperature)
    _ <- IO apply println("Luminosity: " + world.luminosity)
    // TODO print real-time indicators
  } yield ()

  override def resultViewBuiltAndShowed(world: WorldHistory): IO[Unit] = for {
    _ <- IO apply {}
    // TODO: print final indicators
  } yield ()
}
