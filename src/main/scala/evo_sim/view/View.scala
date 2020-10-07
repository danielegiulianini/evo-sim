package evo_sim.view

import evo_sim.model.{Environment, World}

import scala.concurrent.Future

object View {
  private var gui: GUI = _

  def setGUI(g: GUI): Unit = { gui = g }

  def GUIBuilt(): Unit = gui.GUIBuilt()

  def inputReadFromUser(): Future[Environment] = gui.inputReadFromUser().future

  def simulationGUIBuilt(): Unit = gui.simulationGUIBuilt()

  def rendered(world: World): Unit = gui.rendered(world)

  def showResultGUI(world: World): Unit = gui.showResultGUI(world)
}