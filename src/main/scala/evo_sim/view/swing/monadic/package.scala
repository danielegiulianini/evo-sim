package evo_sim.view.swing

import cats.effect.IO
import javax.swing.SwingUtilities

/** A monadic wrapper for Swing and Awt main components that provides an access point to the Swing APIs with
 * a monadic taste. It is independent from the simulator domain and can be potentially re-used outside this
 * project.
 */
package object monadic {
  def invokeAndWaitIO(runnable : => Unit): IO[Unit] = IO {SwingUtilities.invokeAndWait(() => runnable)}
  def invokeLaterIO(runnable : => Unit): IO[Unit] = IO {SwingUtilities.invokeLater(() => runnable)}
}
