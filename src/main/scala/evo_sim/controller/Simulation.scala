package evo_sim.controller

import evo_sim.core.SimulationEngine
import evo_sim.view.{ScalaFXGUI, View}
import javafx.application.Application
import javafx.stage.Stage

trait Simulation {

  def launch(): Unit = {
    SimulationEngine.started()
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
