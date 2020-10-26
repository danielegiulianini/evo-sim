package evo_sim.view.swing.monadic

import java.awt.Component
import java.awt.event.{ComponentListener, MouseListener}

import cats.effect.IO

class ComponentIO(component: Component){
  def addComponentListener(l: ComponentListener) = IO {
    component.addComponentListener(l)
  }
  def addMouseListener(l:MouseListener) = IO {
    component.addMouseListener(l)
  }
  def removeMouseListener(l:MouseListener): Unit = IO {
    component.removeMouseListener(l)
  }
}
