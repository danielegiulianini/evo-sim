package evo_sim.view.swing.monadic

import java.awt.{Component, Container, LayoutManager}

import cats.effect.IO

/**
 * A class that provides a monadic description of the operations supplied by awt's [[Container]] in the form
 * of IO monad in a purely functional fashion.
 * Every Swing's Container could be wrapped by this class, but note that this package provided some ad-hoc factory
 * utilities for the most popular Swing's containers (see [[JPanelIO]], [[JFrameIO]], [[JButtonIO]]).
 * @param component the container that this class wraps.
 * @tparam T the type of the component to be wrapped. and whose methods are to be enhanced with IO description.
 */
class ContainerIO[T<:Container](override val component: T) extends ComponentIO(component) {
  def added(componentToBeAdded: ComponentIO[_<:Component]): IO[Component] = IO {component.add(componentToBeAdded.component)}
  def added(name: String, componentToBeAdded: ComponentIO[ _<:Component]): IO[Component] = IO {    component.add(name, componentToBeAdded.component)}
  def added(componentToBeAdded: ComponentIO[ _<:Component], constraints : Object): IO[Unit] = IO {    component.add(componentToBeAdded.component, constraints)}
  def removed(componentToBeAdded: ComponentIO[ _<:Component]): IO[Unit] = IO {    component.remove(componentToBeAdded.component)  }
  def allRemoved(): IO[Unit] = IO {    component.removeAll()  }
  def layoutSet(mgr : LayoutManager): IO[Unit] = IO {    component.setLayout(mgr)  }
}
