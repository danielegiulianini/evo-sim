package evo_sim.view.swing.monadic

import java.awt.Component

import cats.effect.IO
import javax.swing.{JFrame, SwingUtilities, WindowConstants}


class JFrameIO(val jFrame: JFrame) extends ContainerIO(jFrame) {
  def contentPane() = IO { new ContainerIO (jFrame.getContentPane) }
  def sizeSet(width: Int, height: Int) = IO { jFrame.setSize(width, height)}
  def locationRelativeToSet(c:Component) = IO {jFrame.setLocationRelativeTo(c)}
  def defaultCloseOperationSet(operation:Int): IO[Unit] = IO {jFrame.setDefaultCloseOperation(operation)}
  def titleSet(title: String) = IO{jFrame.setTitle(title)}

  //invoke and wait versions (for finer granularity for task assignment to EDT thread)
  def resizableInvokingAndWaiting(resizable: Boolean): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => jFrame.setResizable(resizable))}
  def visibleInvokingAndWaiting(b: Boolean): IO[Unit] = IO{ SwingUtilities.invokeAndWait(() => jFrame.setVisible(b)) }
  def packedInvokingAndWaiting(): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => jFrame.pack()) }
  def contentPaneInvokingAndWaiting() = IO { SwingUtilities.invokeAndWait( () => new ContainerIO (jFrame.getContentPane)) }
  def sizeSetInvokingAndWaiting(width: Int, height: Int) = IO { SwingUtilities.invokeAndWait( () => jFrame.setSize(width, height))}
  def locationRelativeToSetInvokingAndWaiting(c:Component) = IO {SwingUtilities.invokeAndWait( () => jFrame.setLocationRelativeTo(c))}
  def defaultCloseOperationSetInvokingAndWaiting(operation:Int): IO[Unit] = IO {SwingUtilities.invokeAndWait( ()=> jFrame.setDefaultCloseOperation(operation))}
  def titleSetInvokingAndWaiting(title: String) = IO{SwingUtilities.invokeAndWait( () => jFrame.setTitle(title))}
}

//companion object with utilities
object JFrameIO{
  def apply() = IO { new JFrameIO(new JFrame) }
}