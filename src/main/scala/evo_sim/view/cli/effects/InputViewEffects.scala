package evo_sim.view.cli.effects

import cats.effect.IO
import cats.implicits._
import evo_sim.view.cli.CLIView.NumberOutsideOfRangeException

/** Provides auxiliary functions to create the input cli */
object InputViewEffects {

  def read(text: String, min: Int, max: Int): IO[Int] = {
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

}
