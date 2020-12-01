package evo_sim.view.swing.monadic

import java.awt.{Component, Container, LayoutManager}

import cats.effect.IO

/** A class that provides a monadic description of the operations supplied by awt's [[Container]] in the form
 * of IO monad in a purely functional fashion.
 * Every Swing's Container could be wrapped by this class, but note that this package provided some ad-hoc factory
 * utilities for the most popular Swing's containers (see [[JPanelIO]], [[JFrameIO]], [[JButtonIO]]).
 * @param component the container that this class wraps.
 * @tparam T the type of the component to be wrapped. and whose methods are to be enhanced with IO description.
 */
class ContainerIO[T<:Container](override val component: T) extends ComponentIO(component) {

  /** Returns an [[IO]] containing the code for adding the [[Component]] wrapped in the given [[ComponentIO]] to
   * the [[Container]] wrapped in this instance. It is the monadic counterpart of [[Container#added]].*/
  def added(componentToBeAdded: ComponentIO[_<:Component]): IO[Component] =
    IO {component.add(componentToBeAdded.component)}

  /** Returns an [[IO]] containing the description of a [[java.awt.Container#add]]
   * method invocation. It is the monadic counterpart of [[Container#added]].
   * After adding the specified component at the end of this container, it also notifies
   * the layout manager to add the component to this container's layout using the
   * specified constraints object. */
  def added(componentToBeAdded: ComponentIO[ _<:Component], constraints : Object): IO[Unit] =
    IO {    component.add(componentToBeAdded.component, constraints)}

  /** Returns an [[IO]] containing the description of a [[java.awt.Container#remove]]
   * method invocation.*/
  def removed(componentToBeAdded: ComponentIO[ _<:Component]): IO[Unit] =
    IO {    component.remove(componentToBeAdded.component)  }

  /** Returns an [[IO]] containing the description of a [[java.awt.Container#removeAll]]
   * method invocation.*/
  def allRemoved(): IO[Unit] =
    IO {    component.removeAll()  }

  /** Returns an [[IO]] containing the description of a [[java.awt.Container#setLayout]]
   * method invocation.*/
  def layoutSet(mgr : LayoutManager): IO[Unit] =
    IO {    component.setLayout(mgr)  }
}
