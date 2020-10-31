package evo_sim.view.cli.monadic

import cats.effect.IO

/**
 *
 */
object TextualIO {
  def printlnIO(text:String) = IO { println(text) }
  def println(text:String) = IO { print(text) }
  def readIO() = IO {  scala.io.StdIn.readLine() }
}
