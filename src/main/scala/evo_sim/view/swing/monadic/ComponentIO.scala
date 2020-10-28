package evo_sim.view.swing.monadic

import java.awt.{Component, Dimension}
import java.awt.event.{ComponentListener, MouseListener}

import cats.effect.IO

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
}

//companion object with utilities to be added

