package evo_sim.view.swing.monadic

import java.awt.{Component, Container, Frame}

import cats.effect.IO
import javax.swing.JFrame

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JFrame]] in the form
 * of IO monad in a purely functional fashion.
 * @param component the jFrame that this class wraps.
 */
class JFrameIO(override val component: JFrame) extends ContainerIO(component) {

  def contentPane(): IO[ContainerIO[Container]] = IO { new ContainerIO (component.getContentPane) }
  def resizableSet(resizable: Boolean): IO[Unit] = IO{component.setResizable(resizable)}
  def visibleSet(b: Boolean): IO[Unit] = IO{component.setVisible(b)}
  def packed(): IO[Unit] = IO{component.pack()}
  def sizeSet(width: Int, height: Int): IO[Unit] = IO { component.setSize(width, height)}
  def locationRelativeToSet(c:Component): IO[Unit] = IO {component.setLocationRelativeTo(c)}
  def defaultCloseOperationSet(operation:Int): IO[Unit] = IO {component.setDefaultCloseOperation(operation)}
  def titleSet(title: String): IO[Unit] = IO{component.setTitle(title)}
  def setMaximizedExtendedState(): IO[Unit] = IO{component.setExtendedState(component.getExtendedState | Frame.MAXIMIZED_BOTH)}
}

/** A factory for [[IO]]s containing a JFrameIO instance.*/
object JFrameIO{
  def apply(): IO[JFrameIO] = IO { new JFrameIO(new JFrame) }
}