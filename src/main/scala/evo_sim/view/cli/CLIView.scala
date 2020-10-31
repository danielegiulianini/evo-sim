package evo_sim.view.cli

import cats.effect.IO
import evo_sim.view.cli.effects.InputViewEffects._
import evo_sim.model.Constants._
import evo_sim.model.World.WorldHistory
import evo_sim.model.{Environment, World}
import evo_sim.view.View

/** Provides a view implementation that uses the default CLI */
object CLIView extends View {
  override def inputReadFromUser(): IO[Environment] = for {
    blobs <- read("#Blob", MIN_BLOBS, MAX_BLOBS)
    foods <- read("#Plant", MIN_PLANTS, MAX_PLANTS)
    obstacles <- read("#Obstacle", MIN_OBSTACLES, MAX_OBSTACLES)
    luminosity <- read("Luminosity (cd)", SELECTABLE_MIN_LUMINOSITY, SELECTABLE_MAX_LUMINOSITY)
    temperature <- read("Temperature (°C)", SELECTABLE_MIN_TEMPERATURE, SELECTABLE_MAX_TEMPERATURE)
    days <- read("#Day", MIN_DAYS, MAX_DAYS)
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
    //_ <- IO apply println("Day " + fromIterationsToDays(world.currentIteration) + " of " + fromIterationsToDays(world.totalIterations))
    //_ <- IO apply println("Population " + world.entities.collect { case e: Blob => e }.size)
    //_ <- IO apply println("Temperature: " + world.temperature)
    //_ <- IO apply println("Luminosity: " + world.luminosity)
    _ <- IndicatorsUpdated(world)
  } yield ()

  override def resultViewBuiltAndShowed(world: WorldHistory): IO[Unit] = for {
    _ <- IO apply {}
    // TODO: print final indicators
  } yield ()

  final case class NumberOutsideOfRangeException(private val message: String, private val cause: Throwable = None.orNull)
    extends Exception(message, cause)

  private def IndicatorsUpdated(world:World): IO[Unit] = {
    def printlnIO(text:String) = IO { println(text) }
    for {
      _ <- printlnIO("Temperature: " + world.temperature)
      _ <- printlnIO("Luminosity: " + world.luminosity)
    } yield ()
  }

}
