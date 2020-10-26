package evo_sim.view.swing.monadic

import java.awt.{Component, Container}

import cats.effect.IO

class ContainerIO(container: Container) {
  def add(component: Component) = IO {
    container.add(component)
  }
  def add(name: String, component: Component) = IO {
    container.add(name, component)
  }
}
