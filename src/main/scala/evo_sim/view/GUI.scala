package evo_sim.view

import java.{lang, util}

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.{Environment, World}
import javafx.scene.Parent
import javafx.stage.Stage
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.control.Label
import scalafx.scene.layout._
import scalafx.scene.paint.Color.{Black, Green, Red, Yellow}
import scalafx.stage.Screen
import scalafxml.core.{FXMLView, NoDependencyResolver}

import scala.collection.JavaConverters._
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.concurrent.Promise

trait GUI {
  def inputGUIBuilt(): Unit

  def inputReadFromUser(): Promise[Environment]

  def simulationGUIBuilt(): Unit

  def rendered(world: World): Unit

  def showResultGUI(world: World): Unit
}

case class ScalaFXGUI(stage: Stage) extends GUI {

  private case class Point2DDouble(x: Double, y: Double)

  val inputView: Parent = FXMLView(getClass.getResource("/InputSelector.fxml"),
    NoDependencyResolver)
  val entityPane = new BorderPane
  val barPane = new BorderPane
  val simulatorView: BorderPane = new BorderPane

  override def inputGUIBuilt(): Unit = {
    stage.title = "evo-sim"
    stage.resizable = false
    barPane.setBorder(new Border(new BorderStroke(Black,
      BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths.Default)))
    barPane.top = new Label("Info")
    simulatorView.top = barPane
    simulatorView.center = entityPane
    stage.scene = new Scene(inputView)
    stage.show()
  }

  override def inputReadFromUser(): Promise[Environment] = {
    userInput.environment
  }

  override def simulationGUIBuilt(): Unit = {
    stage.scene = new Scene(simulatorView, Screen.primary.visualBounds.width, Screen.primary.visualBounds.height)
    stage.maximized = true
  }

  override def rendered(world: World): Unit = {
    /* world : screen = model point : x */
    entityPane.children = world.entities.map(e =>
      e.boundingBox match {
        case Circle(point2D, r) => new scalafx.scene.shape.Ellipse {
          centerX = modelToViewRatio(point2D.x, entityPane.width.value, world.width)
          centerY = modelToViewRatio(point2D.y, entityPane.height.value, world.height)
          radiusX = modelToViewRatio(r, entityPane.width.value, world.width)
          radiusY = modelToViewRatio(r, entityPane.height.value, world.height)
          fill = Yellow
        }
        case Rectangle(point2D, w, h) => new scalafx.scene.shape.Rectangle {
          x = modelToViewRatio(point2D.x - w / 2, entityPane.width.value, world.width)
          y = modelToViewRatio(point2D.y - h / 2, entityPane.height.value, world.height)
          width = modelToViewRatio(w, entityPane.width.value, world.width)
          height = modelToViewRatio(h, entityPane.height.value, world.height)
          fill = Red
        }
        case Triangle(point2D, h, a) => new scalafx.scene.shape.Polygon {
          private val vertices = triangleVertices(Triangle(point2D, h, a))
          vertices.productIterator.foreach({
            case p: Point2DDouble => points ++= List(
              modelToViewRatio(p.x, entityPane.width.value, world.width),
              modelToViewRatio(p.y, entityPane.height.value, world.height)
            )
          })
          fill = Green
        }
      })
  }

  override def showResultGUI(world: World): Unit = {
    val linechart = {
      val xAxis = NumberAxis("", 0, 3, 1)
      val yAxis = NumberAxis("Values for Y-Axis", 0, 3, 1)

      val toChartData = (xy: (Int, Int)) => XYChart.Data[Number, Number](xy._1, xy._2)

      val series1 = new XYChart.Series[Number, Number] {
        name = "Death"
        data = Seq(
          (0, 0),
          (1, 2),
          (2, 3),
          (3, 1)).map(toChartData)
      }

      val series2 = new XYChart.Series[Number, Number] {
        name = "Birth"
        data = Seq(
          (0, 0),
          (1, 1),
          (2, 1),
          (3, 0)).map(toChartData)
      }

      val lineChart = new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series1, series2))
      lineChart.setAnimated(true)

      /*def savePng: Unit = {
        val img = lineChart.snapshot(null, new WritableImage(500, 250))
        val file = new File("/tmp/chart.png")
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file)
      }*/

      lineChart
    }
    val resultView = new BorderPane
    resultView.setCenter(linechart)
    stage.scene = new Scene(resultView, 600, 450)
    stage.maximized = false
  }

  private def modelToViewRatio(modelProperty: Double, viewDimension: Double, modelDimension: Double): Double = {
    modelProperty * viewDimension / modelDimension
  }

  private def triangleVertices(tri: Triangle) : (Point2DDouble, Point2DDouble, Point2DDouble) = {
    /** return a tuple3 with the vertices
     *  v1 = center.x, center.y  + radius           -> upper vertices
     *  v2 = center.x - radius, center.y  - radius  -> bottom left vertices
     *  v1 = center.x + radius, center.y  - radius  -> bottom right vertices
     */
    val radius: Double = tri.height/3*2
    (Point2DDouble(tri.point.x, tri.point.y + radius), Point2DDouble(tri.point.x - radius, tri.point.y - radius), Point2DDouble(tri.point.x + radius, tri.point.y - radius))
  }
}

private[view] object userInput {
  val environment: Promise[Environment] = Promise[Environment]()
}
