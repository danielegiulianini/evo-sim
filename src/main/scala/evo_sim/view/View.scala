package evo_sim.view

import evo_sim.model.{Environment, World}

import javafx.stage.Stage
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.control.Label
import scalafx.scene.layout._
import scalafx.scene.paint.Color._
import scalafx.scene.{Parent, Scene}
import scalafx.stage.Screen
import scalafxml.core.{FXMLView, NoDependencyResolver}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Success

object View {
  private var gui: GUI = _

  def setGUI(g: GUI): Unit = { gui = g }

  def GUIBuilt(): Unit = gui.GUIBuilt()

  def inputReadFromUser(): Future[Environment] = gui.inputReadFromUser().future

  def simulationGUIBuilt(): Unit = gui.simulationGUIBuilt()

  def rendered(world: World): Unit = gui.rendered(world)

  def showResultGUI(world: World): Unit = gui.showResultGUI(world)
}