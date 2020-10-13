package evo_sim.view

import evo_sim.model.{Environment, World}

trait GUI {
  def inputGUIBuilt(): Unit

  def inputReadFromUser(): Environment

  def simulationGUIBuilt(): Unit

  def rendered(world: World): Unit

  def showResultGUI(world: World): Unit
}