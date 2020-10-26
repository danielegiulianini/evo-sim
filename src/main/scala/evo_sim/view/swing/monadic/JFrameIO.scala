package evo_sim.view.swing.monadic

import java.awt.Component

import cats.effect.IO
import javax.swing.{JFrame, SwingUtilities, WindowConstants}


class JFrameIO(jFrame: JFrame) {
  //invoke and wait functions (for finer granularity for task assignment to EDT thread)
  def resizableSetInvokingAndWaiting(frame: JFrame): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => frame.setResizable(false))}
  def visibleSetInvokingAndWaiting(b: Boolean): IO[Unit] = IO{ SwingUtilities.invokeAndWait(() => jFrame.setVisible(b)) }
  def packedInvokingAndWaiting(): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => jFrame.pack()) }
  def defaultCloseOperationSetInvokingAndWaiting(operation:Int): IO[Unit] =
    IO {SwingUtilities.invokeAndWait(() => jFrame.setDefaultCloseOperation(operation))}

  def contentPaneGot() = IO { jFrame.getContentPane }
  def sizeSet(width: Int, height: Int) = IO { jFrame.setSize(width, height)}
  def setLocationRelativeTo(c:Component) = IO {jFrame.setLocationRelativeTo(c)}
}

//companion object with utilities to be added
object JFrameIO{
  def apply() = IO { new JFrameIO(new JFrame) }
}