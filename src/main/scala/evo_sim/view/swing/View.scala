package evo_sim.view.swing

import java.awt.event.ActionEvent
import java.awt.{BorderLayout, Dimension, Toolkit}

import evo_sim.model.{Constants, Environment, World}
import evo_sim.view.View
import javax.swing._
import javax.swing.event.ChangeEvent

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object View extends View {

  private val frame = new JFrame("evo-sim")
  private val barPanel = new JPanel
  private val entityPanel = new JPanel

  private val userInput: Promise[Environment] = Promise[Environment]()

  override def inputViewBuiltAndShowed(): Unit = {

    val inputPanel = new JPanel
    val inputLayout = new BoxLayout(inputPanel, BoxLayout.Y_AXIS)
    inputPanel.setLayout(inputLayout)

    def addDataInputRow(text: String, min: Int, max: Int, default: Int): JSlider = {
      val rowPanel = new JPanel
      rowPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10))
      val rowLayout = new BorderLayout()
      rowPanel.setLayout(rowLayout)

      val counter = new JLabel(default.toString)

      val slider: JSlider = new JSlider(min, max, default)
      slider.addChangeListener((e: ChangeEvent) => {
        val source = e.getSource.asInstanceOf[JSlider]
        counter.setText(source.getValue.toString)
      })
      slider.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0))
      slider.setMajorTickSpacing(slider.getMaximum / 5)
      slider.setMinorTickSpacing(1)
      slider.setPaintTicks(true)
      slider.setPaintLabels(true)

      val incrementButton = new JButton("+")
      incrementButton.addActionListener((_: ActionEvent) => if (slider.getValue < max) slider.setValue(slider.getValue + 1))

      val decrementButton = new JButton("-")
      decrementButton.addActionListener((_: ActionEvent) => if (slider.getValue > min) slider.setValue(slider.getValue - 1))

      val infoPanel = new JPanel()
      infoPanel.add(new JLabel(text + ":"))
      infoPanel.add(counter)
      infoPanel.setBorder(BorderFactory.createEmptyBorder((1.5 * counter.getFont.getSize).toInt, 0, 0, 0))
      rowPanel.add(infoPanel, BorderLayout.WEST)

      val commandPanel = new JPanel()
      commandPanel.add(decrementButton)
      commandPanel.add(slider)
      commandPanel.add(incrementButton)
      rowPanel.add(commandPanel, BorderLayout.EAST)

      inputPanel.add(rowPanel)

      slider
    }

    val blobComponent = addDataInputRow("#Blob", Constants.MIN_BLOBS, Constants.MAX_BLOBS, Constants.DEF_BLOBS)
    val foodComponent = addDataInputRow("#Food", Constants.MIN_FOODS, Constants.MAX_FOODS, Constants.DEF_FOODS)
    val obstacleComponent = addDataInputRow("#Obstacle", Constants.MIN_OBSTACLES, Constants.MAX_OBSTACLES, Constants.DEF_OBSTACLES)
    val luminosityComponent = addDataInputRow("Luminosity (cd)", Constants.SELECTABLE_MIN_LUMINOSITY, Constants.SELECTABLE_MAX_LUMINOSITY, Constants.DEFAULT_LUMINOSITY)
    val temperatureComponent = addDataInputRow("Temperature (°C)", Constants.SELECTABLE_MIN_TEMPERATURE, Constants.SELECTABLE_MAX_TEMPERATURE, Constants.DEF_TEMPERATURE)
    val daysComponent = addDataInputRow("#Days", Constants.MIN_DAYS, Constants.MAX_DAYS, Constants.DEF_DAYS)

    val startButton = new JButton("Start")
    startButton.addActionListener((_: ActionEvent) => {
      userInput.success(Environment(
        temperature = temperatureComponent.getValue,
        luminosity = luminosityComponent.getValue,
        initialBlobNumber = blobComponent.getValue,
        initialFoodNumber = foodComponent.getValue,
        initialObstacleNumber = obstacleComponent.getValue,
        daysNumber = daysComponent.getValue
      ))
      frame.setVisible(false)
    })

    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      frame.getContentPane.add(inputPanel, BorderLayout.CENTER)
      frame.getContentPane.add(startButton, BorderLayout.SOUTH)
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
      frame.pack()
      frame.setResizable(false)
      frame.setVisible(true)
    })
  }

  override def inputReadFromUser(): Environment = Await.result(userInput.future, Duration.Inf)

  override def simulationViewBuiltAndShowed(): Unit = {
    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      frame.getContentPane.add(barPanel, BorderLayout.NORTH)
      frame.getContentPane.add(entityPanel, BorderLayout.CENTER)
      frame.setPreferredSize(new Dimension(
        Toolkit.getDefaultToolkit.getScreenSize.width,
        Toolkit.getDefaultToolkit.getScreenSize.height))
      frame.pack()
      frame.setVisible(true)
    })
  }

  override def rendered(world: World): Unit = {
    SwingUtilities.invokeAndWait(() => {
      entityPanel.removeAll()
      entityPanel.add(new ShapesPanel(world))
      frame.pack()
    })
  }

  override def resultViewBuiltAndShowed(world: World): Unit = {
    SwingUtilities.invokeAndWait(() => {
      frame.getContentPane.removeAll()
      // TODO
      frame.setSize(800, 800)
      frame.pack()
    })
  }
}
