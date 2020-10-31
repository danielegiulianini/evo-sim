package evo_sim.view.cli

import cats.effect.IO
import cats.implicits._
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
    _ <- indicatorsUpdated(world)
  } yield ()

  override def resultViewBuiltAndShowed(world: WorldHistory): IO[Unit] = for {
    _ <- IO apply {}
    // TODO: print final indicators
  } yield ()

  final case class NumberOutsideOfRangeException(private val message: String, private val cause: Throwable = None.orNull)
    extends Exception(message, cause)

  private def read(text: String, min: Int, max: Int): IO[Int] = {
    print("Enter " + text + " (between " + min + " and " + max + "): ")
    val asInt: Int = scala.io.StdIn.readLine().checkIfInteger match {
      case Left(_: NumberFormatException) =>
        println("Error: input contains invalid characters.")
        sys.exit(1)
      case Right(result) => result
    }
    asInt.checkIfInRange(min, max) match {
      case Left(_: NumberOutsideOfRangeException) =>
        println("Error: value outside of legal range")
        sys.exit(2)
      case Right(_) => IO pure asInt
    }
  }

  implicit class StringInteger(s: String) {
    def checkIfInteger: Either[NumberFormatException, Int] =
      if (s.matches("-?[0-9]+")) Either.right(s.toInt)
      else Either.left(new NumberFormatException(s + " is not a valid integer."))
  }

  implicit class IntegerInRange(i: Int) {
    def checkIfInRange(min: Int, max: Int): Either[NumberOutsideOfRangeException, Boolean] =
      if (min to max contains i) Either.right(true)
      else Either.left(NumberOutsideOfRangeException(i + " is outside of the legal range"))
  }

  private def indicatorsUpdated(world:World): IO[Unit] = {
    /**
     * Utility function to [[println]] text as IO monad.
     * @param text text to print
     * @return the IO monad that describes the action of println to default output.
     */
    def printlnIO(text:String) = IO { println(text) }
    for {
      _ <- printlnIO("Temperature: " + world.temperature)
      _ <- printlnIO("Luminosity: " + world.luminosity)
    } yield ()
  }

}
