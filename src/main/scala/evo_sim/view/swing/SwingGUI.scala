package evo_sim.view.swing

import java.awt.event.ActionEvent
import java.awt.{BorderLayout, Dimension, Toolkit}

import evo_sim.model.{Environment, World}
import evo_sim.view.GUI
import javax.swing._

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
      new JLabel("Luminosity (cd)"),
      new JLabel("Temperature (°C)"),
      new JLabel("#Days"))
    val labelPanel = new JPanel
    val labelBoxLayout = new BoxLayout(labelPanel, BoxLayout.Y_AXIS)
    labelPanel.setLayout(labelBoxLayout)
    labels.foreach(l => {
      l.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0))
      labelPanel.add(l)
    })
    labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30))

    val blobSlider = new JSlider(0, 300, 50)
    val foodSlider = new JSlider(0, 300, 50)
    val obstacleSlider = new JSlider(0, 100, 20)
    val luminositySlider = new JSlider(10, 200, 100)
    val temperatureSlider = new JSlider(-10, 50, 20)
    val daysSlider = new JSlider(1, 366, 30)
    Set(blobSlider, foodSlider, obstacleSlider, luminositySlider, temperatureSlider, daysSlider).foreach(s => {
      s.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0))
      s.setMajorTickSpacing(s.getMaximum / 5)
      s.setMinorTickSpacing(1)
      s.setPaintTicks(true)
      s.setPaintLabels(true)
    })
    val textFieldPanel = new JPanel
    val textFieldBoxLayout = new BoxLayout(textFieldPanel, BoxLayout.Y_AXIS)
    textFieldPanel.setLayout(textFieldBoxLayout)
    textFieldPanel.add(blobSlider)
    textFieldPanel.add(foodSlider)
    textFieldPanel.add(obstacleSlider)
    textFieldPanel.add(luminositySlider)
    textFieldPanel.add(temperatureSlider)
    textFieldPanel.add(daysSlider)
    textFieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30))

    val startButton = new JButton("Start")
    startButton.addActionListener((_: ActionEvent) => userInput.success(Environment(
      temperature = temperatureSlider.getValue,
      luminosity = luminositySlider.getValue,
      initialBlobNumber = blobSlider.getValue,
      initialFoodNumber = foodSlider.getValue,
      initialObstacleNumber = obstacleSlider.getValue,
      daysNumber = daysSlider.getValue
    )))

    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      frame.getContentPane.add(labelPanel, BorderLayout.WEST)
      frame.getContentPane.add(textFieldPanel, BorderLayout.EAST)
      frame.getContentPane.add(startButton, BorderLayout.SOUTH)
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
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
