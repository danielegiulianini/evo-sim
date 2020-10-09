package evo_sim.core

import evo_sim.model.{Environment, World}
import evo_sim.view.GUI

import scala.concurrent.Future

object ViewModule {
  private var gui: GUI = _

  def setGUI(g: GUI): Unit = { gui = g }

  def GUIBuilt(): Unit = gui.inputGUIBuilt()

  def inputReadFromUser(): Future[Environment] = gui.inputReadFromUser()

  def simulationGUIBuilt(): Unit = gui.simulationGUIBuilt()

  def rendered(world: World): Unit = gui.rendered(world)

  def showResultGUI(world: World): Unit = gui.showResultGUI(world)
}
