package evo_sim.view.swing.monadic

import java.awt.{Component, Container, LayoutManager}

import cats.effect.IO
import javax.swing.SwingUtilities

class ContainerIO[T<:Container](override val component: T) extends ComponentIO(component) {
  def added(componentToBeAdded: ComponentIO[_<:Component]) = IO {component.add(componentToBeAdded.component)}
  def added(name: String, componentToBeAdded: ComponentIO[ _<:Component]) = IO {    component.add(name, componentToBeAdded.component)}
  def added(componentToBeAdded: ComponentIO[ _<:Component], constraints : Object) = IO {    component.add(componentToBeAdded.component, constraints)}
  def removed(componentToBeAdded: ComponentIO[ _<:Component]) = IO {    component.remove(componentToBeAdded.component)  }
  def allRemoved() = IO {    component.removeAll()  }
  def layoutSet(mgr : LayoutManager): IO[Unit] = IO {    component.setLayout(mgr)  }

  //versions with invokeAndWait for finer granularity in thread assignment
  def addedInvokingAndWaiting(componentToBeAdded: ComponentIO[_<:Component]) = IO {  SwingUtilities.invokeAndWait(() =>component.add(componentToBeAdded.component))}
  def addedInvokingAndWaiting(name: String, componentToBeAdded: ComponentIO[ _<:Component]) = IO {    SwingUtilities.invokeAndWait(() =>  component.add(name, componentToBeAdded.component))}
  def addedInvokingAndWaiting(componentToBeAdded: ComponentIO[ _<:Component], constraints : Object) = IO {    SwingUtilities.invokeAndWait(() => component.add(componentToBeAdded.component, constraints))}
  def removedInvokingAndWaiting(componentToBeAdded: ComponentIO[ _<:Component]) = IO {   SwingUtilities.invokeAndWait(() =>  component.remove(componentToBeAdded.component) ) }
  def allRemovedInvokingAndWaiting() = IO {   SwingUtilities.invokeAndWait( () =>  component.removeAll() ) }
}

//companion object with utilities to be added

