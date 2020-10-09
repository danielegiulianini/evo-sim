package evo_sim.view.scala_fx

import evo_sim.controller.Simulation
import evo_sim.core.ViewModule
import javafx.application.Application
import javafx.stage.Stage

object ScalaFXSimulation extends Simulation {
  override def launch(): Unit = {
    Application.launch(classOf[FXInitializer])
  }

  private class FXInitializer extends Application with Simulation {
    override def start(stage: Stage): Unit = {
      ViewModule.setGUI(ScalaFXGUI(stage))
      super.launch()
    }
  }
}
