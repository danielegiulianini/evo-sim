package evo_sim.view.swing.monadic

import java.awt.Component
import java.awt.event.{ComponentListener, MouseListener}

import cats.effect.IO

class ComponentIO(val component: Component){
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
}

//companion object with utilities to be added

