package evo_sim.view.swing.monadic

import java.awt.{Component, Container, LayoutManager}

import cats.effect.IO
import javax.swing.SwingUtilities

class ContainerIO[T<:Container](val container: T) extends ComponentIO(container) {
  def added(component: ComponentIO[_<:Component]) = IO {container.add(component.component)}
  def added(name: String, component: ComponentIO[ _<:Component]) = IO {    container.add(name, component.component)}
  def added(component: ComponentIO[ _<:Component], constraints : Object) = IO {    container.add(component.component, constraints)}
  def removed(component: ComponentIO[ _<:Component]) = IO {    container.remove(component.component)  }
  def allRemoved() = IO {    container.removeAll()  }
  def layoutSet(mgr : LayoutManager): IO[Unit] = IO {    container.setLayout(mgr)  }

  //versions with invokeAndWait for finer granularity in thread assignment
  def addedInvokingAndWaiting(component: ComponentIO[_<:Component]) = IO {  SwingUtilities.invokeAndWait( () =>container.add(component.component))}
  def addedInvokingAndWaiting(name: String, component: ComponentIO[ _<:Component]) = IO {    SwingUtilities.invokeAndWait( () =>  container.add(name, component.component))}
  def addedInvokingAndWaiting(component: ComponentIO[ _<:Component], constraints : Object) = IO {    SwingUtilities.invokeAndWait( () => container.add(component.component, constraints))}
  def removedInvokingAndWaiting(component: ComponentIO[ _<:Component]) = IO {   SwingUtilities.invokeAndWait( () =>  container.remove(component.component) ) }
  def allRemovedInvokingAndWaiting() = IO {   SwingUtilities.invokeAndWait( () =>  container.removeAll() ) }
}

//companion object with utilities to be added

