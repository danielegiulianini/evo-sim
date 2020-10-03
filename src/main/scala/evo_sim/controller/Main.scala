package evo_sim.controller

import evo_sim.view.View
import javafx.application.Application
import javafx.stage.Stage

object Main {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[EvoSim])
  }

  class EvoSim extends Application {
    override def start(stage: Stage): Unit = {
      def GUI = View(stage)

      GUI.GUIBuilt()

      def environment = GUI.inputReadFromUser()
      // GUI.rendered(World(0, Set()))
      println(environment.InitialBlobsNumber)
    }
  }

}
