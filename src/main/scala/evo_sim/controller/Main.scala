package evo_sim.controller

import evo_sim.model.Environment
import evo_sim.view.View
import javafx.application.Application
import javafx.stage.Stage

import scala.concurrent.ExecutionContext

object Main {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[EvoSim])
  }

  class EvoSim extends Application {
    override def start(stage: Stage): Unit = {

      View.inputGUIBuilt(stage)

      val immediateContext: ExecutionContext = new ExecutionContext {
        def execute(runnable: Runnable) {
          runnable.run()
        }

        def reportFailure(cause: Throwable) {}
      }

      View.inputReadFromUser().future.onComplete(e => {
        def env: Environment = e.get
        println("#blob: " + env.initialBlobNumber)
        println("#food: " + env.initialFoodNumber)
        println("#obstacle: " + env.initialObstacleNumber)
        println("temperature: " + env.temperature)
        println("luminosity: " + env.luminosity)
        View.simulationGUIBuilt()
      })(immediateContext)

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
