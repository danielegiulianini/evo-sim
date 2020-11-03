package evo_sim.view.swing

import cats.effect.IO
import javax.swing.SwingUtilities

package object monadic {
  def invokeAndWaitIO(runnable : => Unit): IO[Unit] = IO {SwingUtilities.invokeAndWait(() => runnable)}
  def invokeLaterIO(runnable : => Unit): IO[Unit] = IO {SwingUtilities.invokeLater(() => runnable)}
}
