package evo_sim.view.swing

import java.awt.event.ActionEvent
import java.awt.{BorderLayout, Dimension, Toolkit}
import java.text.NumberFormat

import evo_sim.model.{Environment, World}
import evo_sim.view.GUI
import javax.swing._
import javax.swing.text.NumberFormatter

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

case class SwingGUI() extends GUI {

  private val frame = new JFrame("evo-sim")
  private val barPanel = new JPanel
  private val entityPanel = new JPanel

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
      frame.setSize(800, 800)
      frame.pack()
    })
  }

  override def inputReadFromUser(): Environment = Await.result(userInput.future, Duration.Inf)

  override def simulationGUIBuilt(): Unit = {
    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      frame.getContentPane.add(barPanel, BorderLayout.NORTH)
      frame.getContentPane.add(entityPanel, BorderLayout.CENTER)
      frame.setPreferredSize(new Dimension(
        Toolkit.getDefaultToolkit.getScreenSize.width,
        Toolkit.getDefaultToolkit.getScreenSize.height))
      frame.pack()
    })
  }

  override def rendered(world: World): Unit = {
    SwingUtilities.invokeAndWait(() => {
      entityPanel.removeAll()
      entityPanel.add(new ShapesPanel(world))
      frame.pack()
    })
  }

  override def showResultGUI(world: World): Unit = {
    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      // TODO
      frame.setSize(800, 800)
      frame.pack()
    })
  }
}
