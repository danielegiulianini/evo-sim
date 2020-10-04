package evo_sim.controller

import evo_sim.model.{Environment, World}
import evo_sim.view.View
import javafx.application.Application
import javafx.stage.Stage

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[EvoSim])
  }

  class EvoSim extends Application {
    override def start(stage: Stage): Unit = {
      def GUI = View(stage)

      GUI.GUIBuilt()

      GUI.inputReadFromUser().future.onComplete(_ => println("success"))

      /*
      def environment = GUI.inputReadFromUser()
      def entities: Set[Simulable] = (1 to environment.InitialBlobsNumber).map(e => new BaseBlob(
        boundingBox = BoundingBoxShape.Rectangle.apply(point = (10, 10), width = 5, height = 8),
        life = 100,
        velocity = 50,
        degradationEffect = DegradationEffect.standardDegradation,
        fieldOfViewRadius = 10,
        movementStrategy = null
      )).toSet
      GUI.rendered(World(0, entities))
       */
      //GUI.rendered(World(0, Set()))
    }
  }

}
