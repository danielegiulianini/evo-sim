package evo_sim.view.swing

import java.awt.event.{ActionEvent, ActionListener}

import cats.effect.IO
import javax.swing.SwingUtilities

/** A monadic wrapper for Swing and Awt main components that provides an access point to the Swing APIs with
 * a monadic taste. It is independent from the simulator domain and can be potentially re-used outside this
 * project.
 * Instead of directly executing swing's components' methods and utilities, the classes of this package
 * returns the corresponding [[IO]] monad descriptions, actually decoupling description from execution.
 */
package object monadic {
  def invokingAndWaiting(computation: IO[_]): IO[Unit] = IO {
    SwingUtilities.invokeAndWait(() => computation.unsafeRunSync())
  }
  def invokingLater(computation: IO[_]): IO[Unit] = IO {
    SwingUtilities.invokeLater(() => computation.unsafeRunSync())
  }

  /** Type alias for a monadic listener, i.e. a listener whose behaviour upon ActionEvent is described by an IO
   * monad (possibly result of a composition of IO monads).
   * See [[evo_sim.view.swing.monadic.ExampleWithMonadicVsProceduralListeners]] for examples.*/
  type MonadicActionListener = ActionEvent => IO[Unit]

  /** Implicit utility for converting a by-name (or unevaluated) parameter expression provided by => syntax to
   * [[ActionListener]] for enabling a more concise syntax at call-side when describing listeners that ignores
   * the [[ActionEvent]] triggering.*/
  implicit def unitToActionListener(f: =>Unit): ActionListener = _ => f

  /** Implicit utility for converting a by-name (or unevaluated) parameter expression provided by => syntax to
   * [[MonadicActionListener]] for enabling a more concise syntax at call-side when describing listeners that ignores
   * the [[ActionEvent]] triggering.*/
  implicit def unitToMonadicActionListener(f: => IO[Unit]): MonadicActionListener = _ => f
}
