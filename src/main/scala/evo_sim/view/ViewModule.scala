package evo_sim.view

import evo_sim.model.{Environment, World}
import evo_sim.view.swing.SwingGUI

object ViewModule {
  private val gui: GUI = SwingGUI()

  def GUIBuilt(): Unit = gui.inputGUIBuilt()

  def inputReadFromUser(): Environment = gui.inputReadFromUser()

  def simulationGUIBuilt(): Unit = gui.simulationGUIBuilt()

  def rendered(world: World): Unit = gui.rendered(world)

  def showResultGUI(world: World): Unit = gui.showResultGUI(world)
}
