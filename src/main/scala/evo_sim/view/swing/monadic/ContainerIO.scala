package evo_sim.view.swing.monadic

import java.awt.{Component, Container}

import cats.effect.IO

class ContainerIO(container: Container) {
  def added(component: Component) = IO {
    container.add(component)
  }
  def added(name: String, component: Component) = IO {
    container.add(name, component)
  }
}

//companion object with utilities to be added

