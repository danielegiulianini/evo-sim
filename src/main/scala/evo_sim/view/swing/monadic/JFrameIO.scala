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

  /** Returns an [[IO]] containing the description of a [[JFrame#getContentPane]]
   * method invocation.*/
  def contentPane(): IO[ContainerIO[Container]] = IO { new ContainerIO (component.getContentPane) }

  /** Returns an [[IO]] containing the description of a [[JFrame#setResizable]]
   * method invocation.*/
  def resizableSet(resizable: Boolean): IO[Unit] = IO{component.setResizable(resizable)}

  /** Returns an [[IO]] containing the description of a [[JFrame#setVisible]]
   * method invocation.*/
  def visibleSet(b: Boolean): IO[Unit] = IO{component.setVisible(b)}

  /** Returns an [[IO]] containing the description of a [[JFrame#pack]]
   * method invocation.*/
  def packed(): IO[Unit] = IO{component.pack()}

  /** Returns an [[IO]] containing the description of a [[JFrame#setSize]]
   * method invocation.*/
  def sizeSet(width: Int, height: Int): IO[Unit] = IO { component.setSize(width, height)}

  /** Returns an [[IO]] containing the description of a [[JFrame#setLocationRelativeTo]]
   * method invocation.*/
  def locationRelativeToSet(c:Component): IO[Unit] = IO {component.setLocationRelativeTo(c)}

  /** Returns an [[IO]] containing the description of a [[JFrame#setDefaultCloseOperation]]
   * method invocation.*/
  def defaultCloseOperationSet(operation:Int): IO[Unit] = IO {component.setDefaultCloseOperation(operation)}

  /** Returns an [[IO]] containing the description of a [[JFrame#setTitle]]
   * method invocation.*/
  def titleSet(title: String): IO[Unit] = IO{component.setTitle(title)}

  /** Returns an [[IO]] containing the code for maximizing the state of the JFrame wrapped by this instance.*/
  def setMaximizedExtendedState(): IO[Unit] =
    IO{component.setExtendedState(component.getExtendedState | Frame.MAXIMIZED_BOTH)}
}

/** A factory for [[IO]]s containing a JFrameIO instance.*/
object JFrameIO{
  def apply(): IO[JFrameIO] = IO { new JFrameIO(new JFrame) }
}