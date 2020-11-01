package evo_sim.view.swing.monadic

import java.awt.{Component, Dimension}
import java.awt.event.{ComponentAdapter, ComponentEvent, ComponentListener, MouseListener}

import cats.effect.IO

/**
 *
 * @param component
 * @tparam T
 */
class ComponentIO[T<:Component](val component: T){
  def componentListenerAdded(l: ComponentListener) = IO {
    component.addComponentListener(l)
  }
  def mouseListenerAdded(l:MouseListener) = IO {
    component.addMouseListener(l)
  }
  def mouseListenerRemoved(l:MouseListener): Unit = IO {
    component.removeMouseListener(l)
  }
  def fontGot() = IO {component.getFont}
  def setPreferredSize(d: Dimension) = IO {
    component.setPreferredSize(d)
  }

  def setPreferredSizeInvokingAndWaiting(d: Dimension) = IO {
    component.setPreferredSize(d)
  }

  def addComponentAdapterInvokingAndWaiting() = IO {
    component.addComponentListener(new ComponentAdapter {
      override def componentResized(e: ComponentEvent): Unit =
        component.setPreferredSize(component.getSize)
    })
  }
}

//companion object with utilities to be added

