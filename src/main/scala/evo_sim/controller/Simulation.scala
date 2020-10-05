package evo_sim.controller

import evo_sim.model.EntityBehaviour.{Simulable, SimulableBlob}
import evo_sim.model.{BaseBlob, BoundingBoxShape, DegradationEffect, World}
import evo_sim.view.View
import javafx.application.Application
import javafx.stage.Stage

import scala.concurrent.ExecutionContext

trait Simulation {
  def launch(): Unit
}

object Simulation {
  private var view: View = _

  private def immediateContext: ExecutionContext = new ExecutionContext {
    def execute(runnable: Runnable) {
      runnable.run()
    }

    def reportFailure(cause: Throwable): Unit = {
      cause.printStackTrace()
      System.exit(-1)
    }
  }

  def apply(): Simulation = new FXSimulator

  private class FXSimulator extends Simulation {
    override def launch(): Unit = {
      Application.launch(classOf[FXInitializer])
    }
  }

  private class FXInitializer extends Application {
    override def start(stage: Stage): Unit = {
      view = View(stage)

      // CODICE ... (bisogna portarlo verso l'alto in modo da renderlo generico/indipendente a seconda della view)

      view.inputGUIBuilt()
      view.inputReadFromUser().future.onComplete(e => {
        view.simulationGUIBuilt()
        val environment = e.get
        val blobNum = environment.initialBlobNumber

        def entities: Set[Simulable] = (1 to blobNum).zipWithIndex.map { case (_, i) => new SimulableBlob(BaseBlob(
          boundingBox = BoundingBoxShape.Rectangle.apply(point = (20 + 20 * i, 15 + 15 * i), width = 10, height = 10),
          life = 100,
          velocity = 50,
          degradationEffect = DegradationEffect.standardDegradation,
          fieldOfViewRadius = 10,
          movementStrategy = null /*MovingStrategies.baseMovement?*/))
        }.toSet

        view.rendered(new World(0, entities))
      })(immediateContext)

    }
  }

}