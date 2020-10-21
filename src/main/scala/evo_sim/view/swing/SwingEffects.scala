package evo_sim.view.swing

import java.awt.BorderLayout
import java.awt.event.ActionEvent

import cats.effect.IO
import evo_sim.model.Environment
import javax.swing._
import javax.swing.event.ChangeEvent

import scala.concurrent.Promise

object SwingEffects {

  // component-related
  def componentBorderSet[A <: JComponent](component: A, top: Int, left: Int, bottom: Int, right: Int): IO[Unit] =
    IO apply component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right))

  // frame-related
  def frameCreated(title: String): IO[JFrame] = IO pure new JFrame(title)

  def frameAllRemoved(frame: JFrame): IO[Unit] = IO apply frame.removeAll()

  def frameComponentAdded[A <: Object](frame: JFrame, component: JComponent,
                                       constraint: A = BorderLayout.CENTER): IO[Unit] =
    IO apply frame.getContentPane.add(component, constraint)

  def frameExitOnCloseOperationSet(frame: JFrame): IO[Unit] =
    IO apply frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  def frameIsVisible(frame: JFrame): IO[Unit] = IO apply frame.setVisible(true)

  def frameIsPacked(frame: JFrame): IO[Unit] = IO apply frame.pack()

  def frameIsNotResizable(frame: JFrame): IO[Unit] = IO apply frame.setResizable(false)

  def frameDisposed(frame: JFrame): IO[Unit] = IO apply frame.dispose()

  // panel-related
  def panelCreated: IO[JPanel] = IO pure new JPanel

  def panelBorderLayoutSet(panel: JPanel): IO[Unit] = IO apply panel.setLayout(new BorderLayout())

  def panelVerticalLayoutSet(panel: JPanel): IO[Unit] = IO apply panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))

  def panelComponentsAdded(panel: JPanel, components: JComponent*): IO[Unit] =
    IO pure components.foreach(c => panel.add(c))

  def panelComponentAdded[A <: Object](panel: JPanel, component: JComponent,
                                       constraint: A = BorderLayout.CENTER): IO[Unit] =
    IO apply panel.add(component, constraint)

  // label-related
  def labelCreated(text: String): IO[JLabel] = IO pure new JLabel(text)

  def labelUpdated(label: JLabel, text: String): IO[Unit] = IO apply label.setText(text)

  // slider-related
  def sliderCreated(min: Int, max: Int, default: Int): IO[JSlider] = IO pure new JSlider(min, max, default)

  // button-related
  def buttonCreated(text: String): IO[JButton] = IO pure new JButton(text)

  // input-view-related

  def sliderChangeUpdatesLabelAdded(slider: JSlider, label: JLabel): IO[Unit] = IO apply
    slider.addChangeListener((event: ChangeEvent) => {
      val source = event.getSource.asInstanceOf[JSlider]
      label.setText(source.getValue.toString)
    })

  def buttonEffectUpdatesSliderAdded(button: JButton, slider: JSlider, checkCondition: Int => Boolean,
                                                 updateFunction: Int => Int): IO[Unit] = IO apply {
        button.addActionListener((_: ActionEvent) => {
          if (checkCondition(slider.getValue))
            slider.setValue(updateFunction(slider.getValue))
        })
      }

  def sliderGraphicsUpdated(slider: JSlider): IO[Unit] =
    for {
      _ <- IO.apply(slider.setMajorTickSpacing(slider.getMaximum / 5))
      _ <- IO.apply(slider.setMinorTickSpacing(1))
      _ <- IO.apply(slider.setPaintTicks(true))
      _ <- IO.apply(slider.setPaintLabels(true))
    } yield ()

  def buttonEffectCompletesEnvironmentAdded(button: JButton, promise: Promise[Environment],
                                            temperature: JSlider, luminosity: JSlider,
                                            initialBlobNumber: JSlider, initialFoodNumber: JSlider,
                                            initialObstacleNumber: JSlider, daysNumber: JSlider,
                                            frame: JFrame): IO[Unit] = IO apply
    button.addActionListener((_: ActionEvent) =>
        promise.success(Environment(temperature.getValue, luminosity.getValue, initialBlobNumber.getValue,
          initialFoodNumber.getValue, initialObstacleNumber.getValue, daysNumber.getValue)))

}
