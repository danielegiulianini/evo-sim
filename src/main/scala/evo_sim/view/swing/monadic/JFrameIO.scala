package evo_sim.view.swing.monadic

import java.awt.{Component, Frame}

import cats.effect.IO
import javax.swing.{JFrame, SwingUtilities, WindowConstants}


class JFrameIO(override val component: JFrame) extends ContainerIO(component) {
  def contentPane() = IO { new ContainerIO (component.getContentPane) }
  def sizeSet(width: Int, height: Int) = IO { component.setSize(width, height)}
  def locationRelativeToSet(c:Component) = IO {component.setLocationRelativeTo(c)}
  def defaultCloseOperationSet(operation:Int): IO[Unit] = IO {component.setDefaultCloseOperation(operation)}
  def titleSet(title: String) = IO{component.setTitle(title)}

  //invoke and wait versions (for finer granularity for task assignment to EDT thread)
  def resizableInvokingAndWaiting(resizable: Boolean): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => component.setResizable(resizable))}
  def visibleInvokingAndWaiting(b: Boolean): IO[Unit] = IO{ SwingUtilities.invokeAndWait(() => component.setVisible(b)) }
  def packedInvokingAndWaiting(): IO[Unit] = IO { SwingUtilities.invokeAndWait(() => component.pack()) }
  def contentPaneInvokingAndWaiting() = IO { SwingUtilities.invokeAndWait( () => new ContainerIO (component.getContentPane)) }
  def sizeSetInvokingAndWaiting(width: Int, height: Int) = IO { SwingUtilities.invokeAndWait( () => component.setSize(width, height))}
  def locationRelativeToSetInvokingAndWaiting(c:Component) = IO {SwingUtilities.invokeAndWait( () => component.setLocationRelativeTo(c))}
  def defaultCloseOperationSetInvokingAndWaiting(operation:Int): IO[Unit] = IO {SwingUtilities.invokeAndWait( ()=> component.setDefaultCloseOperation(operation))}
  def titleSetInvokingAndWaiting(title: String) = IO{SwingUtilities.invokeAndWait( () => component.setTitle(title))}
  def setMaximizedExtendedStateInvokeAndWaiting() = IO { component.setExtendedState(component.getExtendedState | Frame.MAXIMIZED_BOTH) }
}

//companion object with utilities
object JFrameIO{
  def apply() = IO { new JFrameIO(new JFrame) }
}