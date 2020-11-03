package evo_sim.view.cli

import cats.effect.IO
import cats.implicits._
import evo_sim.model.world.Constants._
import evo_sim.model.entities.entityStructure.EntityStructure.{Blob, Food}
import evo_sim.model.world.dataProcessing.FinalStats.{averageDuringDay, dayValue, entityCharacteristicAverage, entityDayQuantity}
import evo_sim.model.world.World.{WorldHistory, fromIterationsToDays}
import evo_sim.model.world.{Environment, World}
import evo_sim.view.swing.monadic.ConsoleIO._
import evo_sim.view.View
import evo_sim.view.cli.CLIView.ViewUtils.InputViewUtils.read
import evo_sim.view.cli.CLIView.ViewUtils.SimulationViewUtils.indicatorsUpdated
import evo_sim.view.cli.CLIView.ViewUtils.ResultViewUtils.resultsShow

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
    _ <- indicatorsUpdated(world)
  } yield ()

  override def resultsShowed(world: WorldHistory): IO[Unit] = for {
    _ <- resultsShow(world.reverse)
  } yield ()

  /** Contains some utilities for creating [[CLIView]].*/
  object ViewUtils {
    object InputViewUtils {
      final case class NumberOutsideOfRangeException(private val message: String, private val cause: Throwable = None.orNull)
        extends Exception(message, cause)

      def read(text: String, min: Int, max: Int): IO[Int] = for {
        _ <- printIO("Enter " + text + " (between " + min + " and " + max + "): ")
        readText <- readlnIO()
        value <- handleInputString(readText, min, max)
      } yield value

      def handleInputString(s: String, min: Int, max: Int): IO[Int] = s.checkIfInteger match {
          case Left(_: NumberFormatException) =>
            println("Error: input contains invalid characters.")
            sys.exit(1)
          case Right(result) => result.checkIfInRange(min, max) match {
            case Left(_: NumberOutsideOfRangeException) =>
              println("Error: value outside of legal range")
              sys.exit(2)
            case Right(_) => IO pure result
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
    }
    object SimulationViewUtils {
      def indicatorsUpdated(world: World): IO[Unit] = for {
        _ <- printlnIO("Days: " +
          fromIterationsToDays(world.currentIteration) + "/" +
          fromIterationsToDays(world.totalIterations))
        _ <- printlnIO("Population: " + world.entities.collect{case b: Blob => b}.size)
        _ <- printlnIO("Temperature: " + world.temperature)
        _ <- printlnIO("Luminosity: " + world.luminosity)
        _ <- printlnIO("====================")
      } yield ()
    }
    object ResultViewUtils {
      def resultsShow(history: WorldHistory): IO[Unit] = for {
        _ <- printlnIO("Average velocity: " + totalAverage(averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].velocity))))
        _ <- printlnIO("Average dimension: " + totalAverage(averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].boundingBox.radius))))
        _ <- printlnIO("Average field of view range: " + totalAverage(averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].fieldOfViewRadius))))
        _ <- printlnIO("Survived blobs: " + dayValue(history)(entityDayQuantity(_.isInstanceOf[Blob])).last.toInt)
        _ <- printlnIO("Food left: " + dayValue(history)(entityDayQuantity(_.isInstanceOf[Food])).last.toInt)
      } yield ()
      private def totalAverage(list: List[Double]): Double = list.sum / list.size
    }
  }

}
