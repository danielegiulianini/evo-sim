package evo_sim.view

import evo_sim.model.BoundingBoxShape.{Circle, Rectangle}
import evo_sim.model.{Environment, World}
import javafx.stage.Stage
import scalafx.Includes._
import scalafx.scene.control.Label
import scalafx.scene.layout.{Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii}
import scalafx.scene.paint.Color._
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
  val simulatorView: BorderPane = new BorderPane
  val entityPane = new BorderPane
  val barPane = new BorderPane

  private class FXView(stage: Stage) extends View {

    override def GUIBuilt(): Unit = {
      stage.title = "evo-sim"
      stage.width = 600
      stage.height = 450
      barPane.setBorder(new Border(new BorderStroke(Black,
        BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths.Default)))
      barPane.top = new Label("Info")
      simulatorView.top = barPane
      simulatorView.center = entityPane
    }

    override def inputReadFromUser(): Environment = {
      stage.scene = new Scene(inputView)
      stage.show()
      // TODO: handle other inputs
      // TODO: fix await result
      val env = new Environment(
        temperature = 30,
        luminosity = 50,
        initialBlobsNumber = Await.result(userInput.environment.future, Duration.Inf),
        initialFoodNumber = 0,
        initialObstacleNumber = 0)

      stage.scene = new Scene(simulatorView)
      stage.show()
      env
    }

    override def rendered(world: World): Unit = {
      entityPane.children = world.entities.map(e =>
        e.boundingBox match {
          case Circle((x, y), r) => new scalafx.scene.shape.Circle {
            centerX = x
            centerY = y
            radius = r
            fill = Red
          }
          case Rectangle((xCord, yCord), w, h) => new scalafx.scene.shape.Rectangle {
            x = xCord
            y = yCord
            width = w
            height = h
            fill = Red
          }
          // TODO: Triangle (how to obtain point, Polygon doesn't have fill attribute)
          case _ => scalafx.scene.shape.Polygon()
        })
    }

  }

}

object userInput {
  val environment: Promise[Integer] = Promise[Integer]()
}