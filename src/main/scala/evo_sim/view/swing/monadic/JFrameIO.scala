package evo_sim.view.swing.monadic

import java.awt.{Dimension, Toolkit}

import cats.effect.IO
import javax.swing.{JFrame, SwingUtilities}

class JFrameIO(jFrame: JFrame) extends JComponentIO {
  def resizedInvokingAndWaiting(frame: JFrame): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => frame.setResizable(false))}
  def visibleSetInvokingAndWaiting(b: Boolean): IO[Unit] = IO{ SwingUtilities.invokeAndWait(() => jFrame.setVisible(b)) }
  def packedInvokingAndWaiting(): IO[Unit] = IO apply SwingUtilities.invokeAndWait(() => jFrame.pack())


}
