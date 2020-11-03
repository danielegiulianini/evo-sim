package evo_sim.view.swing.monadic

import java.awt.{Component, Dimension, Font}
import java.awt.event.{ComponentAdapter, ComponentEvent, ComponentListener, MouseListener}

import cats.effect.IO

/**
 * A class that provides a monadic description of the operations supplied by awt's [[Component]] in the form
 * of IO monad in a purely functional fashion.
 * Every Swing's Component could be wrapped by this class, but note that this package provided some ad-hoc factory
 * utilities for the most popular Swing's components (see [[JPanelIO]], [[JFrameIO]], [[JButtonIO]]).
 * @param component the component that this class wraps.
 * @tparam T the type of the component to be wrapped. and whose methods are to be enhanced with IO description.
 */
class ComponentIO[T<:Component](val component: T){

  def componentListenerAdded(l: ComponentListener): IO[Unit] =
    IO { component.addComponentListener(l) }
  def mouseListenerAdded(l:MouseListener): IO[Unit] =
    IO {component.addMouseListener(l) }
  def mouseListenerRemoved(l:MouseListener): Unit =
    IO { component.removeMouseListener(l) }
  def fontGot(): IO[Font] = IO {component.getFont}
  def setPreferredSize(d: Dimension): IO[Unit] =
    IO {component.setPreferredSize(d) }

  def setPreferredSizeInvokingAndWaiting(d: Dimension): IO[Unit] =
    invokeAndWaitIO(component.setPreferredSize(d))
  def addComponentAdapterInvokingAndWaiting(): IO[Unit] =
   invokeAndWaitIO(
      component.addComponentListener(new ComponentAdapter {
        override def componentResized(e: ComponentEvent): Unit =
          component.setPreferredSize(component.getSize)
      })
   )
}
