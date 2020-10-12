package evo_sim.view.swing

import java.awt.event.ActionEvent
import java.awt.geom.{Ellipse2D, Rectangle2D}
import java.awt.{BorderLayout, Dimension, Graphics, Polygon}
import java.text.NumberFormat

import evo_sim.model.BoundingBox.{Circle, Rectangle, Triangle}
import evo_sim.model.{Environment, World}
import evo_sim.view.GUI
import javax.swing._
import javax.swing.text.NumberFormatter

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

case class SwingGUI() extends GUI {

  private val frame = new JFrame("evo-sim")
  private val userInput: Promise[Environment] = Promise[Environment]()

  SwingUtilities.invokeAndWait(() => frame.setVisible(true))

  override def inputGUIBuilt(): Unit = {
    val labels = List(
      new JLabel("#Blob"),
      new JLabel("#Food"),
      new JLabel("#Obstacle"),
      new JLabel("Luminosity (L)"),
      new JLabel("Temperature (°C)"),
      new JLabel("#Days"))
    val labelPanel = new JPanel
    val labelBoxLayout = new BoxLayout(labelPanel, BoxLayout.Y_AXIS)
    labelPanel.setLayout(labelBoxLayout)
    labels.foreach(l => {
      l.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0))
      labelPanel.add(l)
    })
    labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30))

    val format = NumberFormat.getInstance
    val formatter = new NumberFormatter(format)
    formatter.setValueClass(classOf[Integer])
    formatter.setMinimum(0)
    formatter.setMaximum(Integer.MAX_VALUE)
    formatter.setAllowsInvalid(false)
    formatter.setCommitsOnValidEdit(true)

    val blobTextField = new JFormattedTextField(formatter)
    val foodTextField = new JFormattedTextField(formatter)
    val obstacleTextField = new JFormattedTextField(formatter)
    val luminosityTextField = new JFormattedTextField(formatter)
    val temperatureField = new JFormattedTextField(formatter)
    val daysField = new JFormattedTextField(formatter)
    blobTextField.setColumns(10)
    foodTextField.setColumns(10)
    obstacleTextField.setColumns(10)
    luminosityTextField.setColumns(10)
    temperatureField.setColumns(10)
    daysField.setColumns(10)
    val textFieldPanel = new JPanel
    val textFieldBoxLayout = new BoxLayout(textFieldPanel, BoxLayout.Y_AXIS)
    textFieldPanel.setLayout(textFieldBoxLayout)
    textFieldPanel.add(blobTextField)
    textFieldPanel.add(foodTextField)
    textFieldPanel.add(obstacleTextField)
    textFieldPanel.add(luminosityTextField)
    textFieldPanel.add(temperatureField)
    textFieldPanel.add(daysField)
    textFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30))

    val startButton = new JButton("Start")
    startButton.addActionListener((_: ActionEvent) => userInput.success(Environment(
      temperature = blobTextField.getText().toInt,
      luminosity = luminosityTextField.getText().toInt,
      initialBlobNumber = blobTextField.getText().toInt,
      initialFoodNumber = foodTextField.getText().toInt,
      initialObstacleNumber = obstacleTextField.getText().toInt,
      daysNumber = daysField.getText().toInt
    )))

    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      frame.getContentPane.add(labelPanel, BorderLayout.WEST)
      frame.getContentPane.add(textFieldPanel, BorderLayout.EAST)
      frame.getContentPane.add(startButton, BorderLayout.SOUTH)
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      frame.setSize(new Dimension(600, 400))
      frame.pack()
    })
  }

  override def inputReadFromUser(): Environment = Await.result(userInput.future, Duration.Inf)

  override def simulationGUIBuilt(): Unit = {
    val barPanel = new JPanel
    val entityPanel = new JPanel
    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      frame.getContentPane.add(barPanel, BorderLayout.NORTH)
      frame.getContentPane.add(entityPanel, BorderLayout.CENTER)
      frame.setSize(new Dimension(1280, 720))
      frame.pack()
    })
  }

  override def rendered(world: World): Unit = {
    SwingUtilities.invokeAndWait(() => {
      /*
      world.entities.foreach(e => frame.getContentPane.getComponent(0).add(e.boundingBox match {
        case Circle(point2D, r) => new Ellipse2D.Double(
          centerX = modelToViewRatio(point2D.x, entityPane.width.value, world.width)
          centerY = modelToViewRatio(point2D.y, entityPane.height.value, world.height)
          radiusX = modelToViewRatio(r, entityPane.width.value, world.width)
          radiusY = modelToViewRatio(r, entityPane.height.value, world.height)
          fill = Yellow)
        case Rectangle(point2D, w, h) => new Rectangle2D.Double(
          x = modelToViewRatio(point2D.x - w / 2, entityPane.width.value, world.width)
          y = modelToViewRatio(point2D.y - h / 2, entityPane.height.value, world.height)
          width = modelToViewRatio(w, entityPane.width.value, world.width)
          height = modelToViewRatio(h, entityPane.height.value, world.height)
          fill = Red)
        case Triangle(point2D, h, a) => new Polygon(
          private val vertices = triangleVertices(Triangle(point2D, h, a))
          vertices.productIterator.foreach({
            case p: Point2DDouble => points ++= List(
              modelToViewRatio(p.x, entityPane.width.value, world.width),
              modelToViewRatio(p.y, entityPane.height.value, world.height)
            )
          })
          fill = Green)
      }))
      */
    })
  }

  override def showResultGUI(world: World): Unit = {
    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      // TODO
      frame.pack()
    })
  }
}
