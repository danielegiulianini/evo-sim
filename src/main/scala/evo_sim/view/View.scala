package evo_sim.view

import evo_sim.model.{Environment, World}
import javafx.stage.Stage
import scalafx.Includes._
import scalafx.scene.{Parent, Scene}
import scalafxml.core.{FXMLView, NoDependencyResolver}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

trait View {
  def rendered(world: World): Unit

  def GUIBuilt(): Unit

  def inputReadFromUser(): Environment
}

object View {
  def apply(stage: Stage): View = new FXView(stage)

  private val inputView: Parent = FXMLView(getClass.getResource("/InputSelector.fxml"),
    NoDependencyResolver)

  private class FXView(stage: Stage) extends View {

    override def GUIBuilt(): Unit = {
      stage.title = "evo-sim"
    }

    override def inputReadFromUser(): Environment = {
      stage.scene = new Scene(inputView)
      stage.show()
      // TODO: handle other inputs
      new Environment(
        temperature = 30,
        luminosity = 50,
        initialBlobsNumber = Await.result(userInput.environment.future, Duration.Inf),
        initialFoodNumber = 0,
        initialObstacleNumber = 0)
    }

    override def rendered(world: World): Unit = ???

  }

}

object userInput {
  val environment: Promise[Integer] = Promise[Integer]()
}