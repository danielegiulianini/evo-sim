package evo_sim.controller

import evo_sim.core.SimulationEngine
import evo_sim.model.Entities.BaseBlob
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.{BoundingBoxShape, DegradationEffect, World}
import evo_sim.view.{ScalaFXGUI, View}
import javafx.application.Application
import javafx.stage.Stage

import scala.concurrent.ExecutionContext

trait Simulation {

  def launch(): Unit = {

    SimulationEngine.started()

    /*
    def immediateContext: ExecutionContext = new ExecutionContext {
      def execute(runnable: Runnable) {
        runnable.run()
      }

      def reportFailure(cause: Throwable): Unit = {
        cause.printStackTrace()
        System.exit(-1)
      }
    }

    View.GUIBuilt()
    View.inputReadFromUser().onComplete(e => {
      View.simulationGUIBuilt()
      val environment = e.get
      val blobNum = environment.initialBlobNumber

      def entities: Set[SimulableEntity] = (1 to blobNum).zipWithIndex.map { case (_, i) => BaseBlob(
        boundingBox = BoundingBoxShape.Rectangle.apply(point = (20 + 20 * i, 15 + 15 * i), width = 10, height = 10),
        life = 100,
        velocity = 50,
        degradationEffect = DegradationEffect.standardDegradation,
        fieldOfViewRadius = 10,
        movementStrategy = null /*MovingStrategies.baseMovement?*/)
      }.toSet

      View.rendered(new World(0, entities))
    })(immediateContext)
     */

  }

}

object ScalaFXSimulation extends Simulation {
  override def launch(): Unit = {
    Application.launch(classOf[FXInitializer])
  }

  private class FXInitializer extends Application with Simulation {

    override def start(stage: Stage): Unit = {
      View.setGUI(ScalaFXGUI(stage))
      super.launch()
    }
  }
}
