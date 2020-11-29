package evo_sim.view.swing.monadic

import java.awt.{Component, Dimension, Font}
import java.awt.event.{ComponentAdapter, ComponentEvent, ComponentListener, MouseListener}

import cats.effect.IO

/** A class that provides a monadic description of the operations supplied by awt's [[Component]] in the form
 * of IO monad in a purely functional fashion.
 * Every Swing's Component could be wrapped by this class, but note that this package provided some ad-hoc factory
 * utilities for the most popular Swing's components (see [[JPanelIO]], [[JFrameIO]], [[JButtonIO]]).
 * @param component the component that this class wraps.
 * @tparam T the type of the component to be wrapped whose methods are to be enhanced with [[IO]] description.
 */
class ComponentIO[T<:Component](val component: T){
  //TODO procedural vs monadic for listeners
  /** Returns an [[IO]] containing the description of a [[java.awt.Component#addMouseListener]]
   * method invocation.*/
  def mouseListenerAdded(l:MouseListener): IO[Unit] = IO {component.addMouseListener(l) }

  /** Returns an [[IO]] containing the description of a [[java.awt.Component#removeMouseListener]]
   * method invocation.*/
  def mouseListenerRemoved(l:MouseListener): Unit = IO { component.removeMouseListener(l) }

  /** Returns an [[IO]] containing the description of a [[java.awt.Component#addComponentListener]]
   * method invocation. */
  def componentListenerAdded(l: ComponentListener): IO[Unit] = IO { component.addComponentListener(l) }

  /** Returns an [[IO]] containing the code for registering a [[ComponentAdapter]] listener for handling
   * component events from this component*/
  def componentAdapterAdded(): IO[Unit] = IO {
    component.addComponentListener(new ComponentAdapter {
      override def componentResized(e: ComponentEvent): Unit =
        component.setPreferredSize(component.getSize)
    })}

  /** Returns an [[IO]] containing the description of a [[java.awt.Component#removeComponentListener]]
   * method invocation. */
  def componentListenerRemoved(l: ComponentListener): IO[Unit] = IO { component.removeComponentListener(l) }

  /** Returns an [[IO]] containing the description of a [[java.awt.Component#getFont]]
   * method invocation. */
  def fontGot(): IO[Font] = IO {component.getFont}

  /** Returns an [[IO]] containing the description of a [[java.awt.Component#setPreferredSize]]
   * method invocation. */
  def setPreferredSize(d: Dimension): IO[Unit] = IO {component.setPreferredSize(d) }

}
