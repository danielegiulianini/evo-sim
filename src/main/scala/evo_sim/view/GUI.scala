package evo_sim.view

import evo_sim.model.{Environment, World}

import scala.concurrent.Future

trait GUI {
  def inputGUIBuilt(): Unit

  def inputReadFromUser(): Future[Environment]

  def simulationGUIBuilt(): Unit

  def rendered(world: World): Unit

  def showResultGUI(world: World): Unit
}
