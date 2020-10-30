package evo_sim.view.cli

import cats.effect.IO
import evo_sim.model.World.{WorldHistory, fromIterationsToDays}
import evo_sim.model.{Environment, World}
import evo_sim.model.Constants._
import evo_sim.view.View

/** Provides a view implementation that uses the default CLI */
object CLIView extends View {
  override def inputReadFromUser(): IO[Environment] = for {
    blobs <- read("#Blob") min MIN_BLOBS max MAX_BLOBS
    foods <- read("#Plant") min MIN_PLANTS max MAX_PLANTS
    obstacles <- read("#Obstacle") min MIN_OBSTACLES max MAX_OBSTACLES
    luminosity <- read("Luminosity (cd)") min MIN_LUMINOSITY max MAX_LUMINOSITY
    temperature <- read("Temperature (°C)") min MIN_TEMPERATURE max MAX_TEMPERATURE
    days <- read("#Day") min MIN_DAYS max MAX_DAYS
    environment <- IO pure Environment(
      temperature = temperature,
      luminosity = luminosity,
      initialBlobNumber = blobs,
      initialPlantNumber = foods,
      initialObstacleNumber = obstacles,
      daysNumber = days
    )
    _ <- IO apply println(environment)
  } yield environment

  override def rendered(world: World): IO[Unit] = for {
    _ <- IO apply println("Day " + fromIterationsToDays(world.currentIteration) + " of " +
      fromIterationsToDays(world.totalIterations))
    //_ <- indicatorsUpdated(world)
  } yield ()

  override def resultViewBuiltAndShowed(world: WorldHistory): IO[Unit] = for {
    _ <- IO apply {}
    // TODO: print final indicators
  } yield ()

  private def indicatorsUpdated(world:World): IO[Unit] = {
    def printlnIO(text:String) = IO { println(text) }
    for {
      _ <- printlnIO("Temperature: " + world.temperature)
      _ <- printlnIO("Luminosity: " + world.luminosity)
    } yield ()
  }

    private def read(what: String): IO[Int] = for (_ <- IO { print("Enter " + what + ": ") }) yield scala.io.StdIn.readInt()

  private implicit class Ranges(in: IO[Int]) {
    def min(m: Int): IO[Int] = for (i <- in) yield i.max(m)
    def max(m: Int): IO[Int] = for (i <- in) yield i.min(m)
  }

}
