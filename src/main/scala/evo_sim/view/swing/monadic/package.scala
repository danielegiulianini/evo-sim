package evo_sim.view.swing

import java.awt.event.{ActionEvent, ActionListener}

import cats.effect.IO
import javax.swing.SwingUtilities

/** A monadic wrapper for Swing and Awt main components that provides an access point to the Swing APIs with
 * a monadic taste. It is independent from the simulator domain and can be potentially re-used outside this
 * project.
 */
package object monadic {
  def invokingAndWaiting(computation: IO[_]): IO[Unit] = IO {
    SwingUtilities.invokeAndWait(() => computation.unsafeRunSync())
  }
  def invokingLater(computation: IO[_]): IO[Unit] = IO {
    SwingUtilities.invokeLater(() => computation.unsafeRunSync())
  }

  implicit def unitToActionListener(f: =>Unit): ActionListener = _ => f
  type MonadicActionListener = ActionEvent => IO[Unit]
  implicit def unitToMonadicActionListener(f: => IO[Unit]): MonadicActionListener = _ => f
}
