package evo_sim.view

import evo_sim.model.BoundingBoxShape.{Circle, Rectangle, Triangle, triangleVertices}
import evo_sim.model.{Environment, World}
import javafx.stage.Stage
import scalafx.Includes._
import scalafx.scene.control.Label
import scalafx.scene.layout._
import scalafx.scene.paint.Color._
import scalafx.scene.{Parent, Scene}
import scalafxml.core.{FXMLView, NoDependencyResolver}

import scala.concurrent.Promise

trait View {
  def inputGUIBuilt(): Unit

  def inputReadFromUser(): Promise[Environment]

  def simulationGUIBuilt(): Unit

  def rendered(world: World): Unit
}

object View {
  def apply(stage: Stage): View = new FXView(stage)

  private class FXView(stage: Stage) extends View {

    private var primaryStage: Stage = stage
    private val inputView: Parent = FXMLView(getClass.getResource("/InputSelector.fxml"),
      NoDependencyResolver)
    private val simulatorView: BorderPane = new BorderPane
    private val entityPane = new BorderPane
    private val barPane = new BorderPane

    override def inputGUIBuilt(): Unit = {
      primaryStage = stage
      primaryStage.title = "evo-sim"
      barPane.setBorder(new Border(new BorderStroke(Black,
        BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths.Default)))
      barPane.top = new Label("Info")
      simulatorView.top = barPane
      simulatorView.center = entityPane
    }

    override def inputReadFromUser(): Promise[Environment] = {
      primaryStage.scene = new Scene(inputView)
      primaryStage.resizable = false
      primaryStage.show()
      userInput.environment
    }

    override def simulationGUIBuilt(): Unit = {
      primaryStage.scene = new Scene(simulatorView, 600, 450)
    }

    override def rendered(world: World): Unit = {
      entityPane.children = world.entities.map(e =>
        e.structure.boundingBox match {
          case Circle((x, y), r) => new scalafx.scene.shape.Circle {
            centerX = x
            centerY = y
            radius = r
            fill = Yellow
          }
          case Rectangle((xCord, yCord), w, h) => new scalafx.scene.shape.Rectangle {
            x = xCord
            y = yCord
            width = w
            height = h
            fill = Red
          }
          case Triangle((xCord, yCord), h, a) => new scalafx.scene.shape.Polygon {
            private val vertices = triangleVertices(Triangle((xCord, yCord), h, a))
            points.addAll(vertices._1, vertices._2, vertices._3, vertices._4, vertices._5, vertices._6)
            fill = Green
          }
        })
    }

  }

}

private[view] object userInput {
  val environment: Promise[Environment] = Promise[Environment]()
}