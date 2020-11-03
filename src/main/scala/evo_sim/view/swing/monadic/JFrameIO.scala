package evo_sim.view.swing.monadic

import java.awt.{Component, Container, Frame}

import cats.effect.IO
import javax.swing.{JFrame, SwingUtilities}

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JFrame]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jFrame that this class wraps.
 */
class JFrameIO(override val component: JFrame) extends ContainerIO(component) {
  def contentPane(): IO[ContainerIO[Container]] = IO { new ContainerIO (component.getContentPane) }
  def sizeSet(width: Int, height: Int): IO[Unit] = IO { component.setSize(width, height)}
  def locationRelativeToSet(c:Component): IO[Unit] = IO {component.setLocationRelativeTo(c)}
  def defaultCloseOperationSet(operation:Int): IO[Unit] = IO {component.setDefaultCloseOperation(operation)}
  def titleSet(title: String): IO[Unit] = IO{component.setTitle(title)}

  //invoke and wait versions (for finer granularity for task assignment to EDT thread)
  def resizableInvokingAndWaiting(resizable: Boolean): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => component.setResizable(resizable))}
  def visibleInvokingAndWaiting(b: Boolean): IO[Unit] = IO{ SwingUtilities.invokeAndWait(() => component.setVisible(b)) }
  def packedInvokingAndWaiting(): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => component.pack()) }
  def contentPaneInvokingAndWaiting(): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => new ContainerIO (component.getContentPane)) }
  def sizeSetInvokingAndWaiting(width: Int, height: Int): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => component.setSize(width, height))}
  def locationRelativeToSetInvokingAndWaiting(c:Component): IO[Unit] = IO {SwingUtilities.invokeAndWait(() => component.setLocationRelativeTo(c))}
  def defaultCloseOperationSetInvokingAndWaiting(operation:Int): IO[Unit] = IO {SwingUtilities.invokeAndWait( ()=> component.setDefaultCloseOperation(operation))}
  def titleSetInvokingAndWaiting(title: String): IO[Unit] = IO{SwingUtilities.invokeAndWait(() => component.setTitle(title))}
  def setMaximizedExtendedStateInvokeAndWaiting(): IO[Unit] = IO { component.setExtendedState(component.getExtendedState | Frame.MAXIMIZED_BOTH) }
}

/** Factory for JFrameIO instances*/
object JFrameIO{
  def apply(): IO[JFrameIO] = IO { new JFrameIO(new JFrame) }
}