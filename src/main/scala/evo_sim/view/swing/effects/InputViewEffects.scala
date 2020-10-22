package evo_sim.view.swing.effects

import java.awt.BorderLayout
import java.awt.event.ActionEvent

import cats.effect.IO
import evo_sim.model.Environment
import SwingEffects._
import javax.swing.event.ChangeEvent
import javax.swing._

import scala.concurrent.Promise

object InputViewEffects {

  def createDataInputRow(inputPanel: JPanel, text: String, minValue: Int, maxValue: Int,
                         defaultValue: Int): IO[JSlider] = {
    for {
      rowPanel <- panelCreated
      _ <- borderSet(rowPanel, 10, 10, 10, 10)
      _ <- borderLayoutSet(rowPanel)
      description <- labelCreated(text + ":")
      counter <- labelCreated(defaultValue.toString)
      slider <- sliderCreated(minValue, maxValue, defaultValue)
      _ <- changeUpdatesLabelListenerAdded(slider, counter)
      _ <- borderSet(slider, 5, 0, 5, 0)
      _ <- graphicsSet(slider)
      increment <- buttonCreated("+")
      _ <- clickUpdatesSliderListenerAdded(increment, slider, _ < maxValue, _ + 1)
      decrement <- buttonCreated("-")
      _ <- clickUpdatesSliderListenerAdded(decrement, slider, _ > minValue, _ - 1)
      infoPanel <- panelCreated
      _ <- componentsAdded(infoPanel, description, counter)
      _ <- borderSet(infoPanel, (1.5 * counter.getFont.getSize).toInt, 0, 0, 0)
      commandPanel <- panelCreated
      _ <- componentsAdded(commandPanel, decrement, slider, increment)
      _ <- componentAdded(rowPanel, infoPanel, BorderLayout.WEST)
      _ <- componentAdded(rowPanel, commandPanel, BorderLayout.EAST)
      _ <- componentAdded(inputPanel, rowPanel)
    } yield slider
  }

  def changeUpdatesLabelListenerAdded(slider: JSlider, label: JLabel): IO[Unit] = IO apply
    slider.addChangeListener((event: ChangeEvent) => {
      val source = event.getSource.asInstanceOf[JSlider]
      label.setText(source.getValue.toString)
    })

  def clickUpdatesSliderListenerAdded(button: JButton, slider: JSlider, checkCondition: Int => Boolean,
                                      updateFunction: Int => Int): IO[Unit] = IO apply {
    button.addActionListener((_: ActionEvent) => {
      if (checkCondition(slider.getValue))
        slider.setValue(updateFunction(slider.getValue))
    })
  }

  def graphicsSet(slider: JSlider): IO[Unit] = for {
    _ <- IO.apply(slider.setMajorTickSpacing(slider.getMaximum / 5))
    _ <- IO.apply(slider.setMinorTickSpacing(1))
    _ <- IO.apply(slider.setPaintTicks(true))
    _ <- IO.apply(slider.setPaintLabels(true))
  } yield ()

  def clickCompletesEnvironmentListenerAdded(button: JButton, promise: Promise[Environment],
                                             temperature: JSlider, luminosity: JSlider,
                                             initialBlobNumber: JSlider, initialFoodNumber: JSlider,
                                             initialObstacleNumber: JSlider, daysNumber: JSlider,
                                             frame: JFrame): IO[Unit] = IO apply
    button.addActionListener((_: ActionEvent) =>
      promise.success(Environment(temperature.getValue, luminosity.getValue, initialBlobNumber.getValue,
        initialFoodNumber.getValue, initialObstacleNumber.getValue, daysNumber.getValue)))

}
