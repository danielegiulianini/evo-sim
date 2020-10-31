package evo_sim.utils

import cats.effect.IO

/** A module with utilities for performing basic console input and output in a purely functional way.
 */
object ConsoleIO {
  /**
   * Utility function to [[println]] text as IO monad.
   *
   * @param text text to print
   * @return the [[IO]] monad that describes the action of printlning to default output.
   */
  def printlnIO(text: String) = IO {
    println(text)
  }

  /**
   * Utility function to [[print]] text as IO monad.
   *
   * @param text text to print
   * @return the [[IO]] monad that describes the action of printing to default output.
   */
  def printIO(text: String) = IO {
    print(text)
  }

  /**
   * Utility function to [[scala.io.StdIn.readLine]] from default input as IO monad.
   * @return the [[IO]] monad that describes the action of performing a [[readLine]] from default input.
   */
  def readIO() = IO {
    scala.io.StdIn.readLine()
  }
}
