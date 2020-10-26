package evo_sim.view.swing.effects

import java.awt.BorderLayout
import java.awt.event.ActionEvent

import cats.effect.IO
import evo_sim.model.Environment
import evo_sim.view.swing.monadic.{JButtonIO, JLabelIO, JPanelIO, JSliderIO}
import javax.swing._
import javax.swing.event.ChangeEvent

import scala.concurrent.Promise

object InputViewEffects {

  def createDataInputRow(inputPanel: JPanelIO, text: String, minValue: Int, maxValue: Int,
                         defaultValue: Int): IO[JSliderIO] = {
    for {
      rowPanel <- JPanelIO()
      //border <- emptyBorderCreated(10, 10, 10, 10)
      //_ <- rowPanel.borderSet()
      description <- JLabelIO()
      _ <- description.textSet(text + ":")
      counter <- JLabelIO()
      _ <- counter.textSet(defaultValue.toString)
      slider <- JSliderIO()
      _ <- slider.minimumSet(minValue)
      _ <- slider.maximumSet(maxValue)
      // _ <- slider.defaultSet(defaultValue)
      _ <- changeUpdatesLabelListenerAdded(slider, counter)
      //_ <- borderSet(slider, 5, 0, 5, 0)
      //_ <- IO.apply(slider.setMajorTickSpacing(slider.getMaximum / 5))
      //_ <- IO.apply(slider.setMinorTickSpacing(1))
      //_ <- IO.apply(slider.setPaintTicks(true))
      //_ <- IO.apply(slider.setPaintLabels(true))
      increment <- JButtonIO("+")
      _ <- clickUpdatesSliderListenerAdded(increment, slider, _ < maxValue, _ + 1)
      decrement <- JButtonIO("-")
      _ <- clickUpdatesSliderListenerAdded(decrement, slider, _ > minValue, _ - 1)
      infoPanel <- JPanelIO()
      _ <- infoPanel.added(description)
      _ <- infoPanel.added(counter)
      //_ <- borderSet(infoPanel, (1.5 * counter.getFont.getSize).toInt, 0, 0, 0)
      commandPanel <- JPanelIO()
      _ <- commandPanel.added(decrement)
      _ <- commandPanel.added(slider)
      _ <- commandPanel.added(increment)
      _ <- rowPanel.added(infoPanel, BorderLayout.WEST)
      _ <- rowPanel.added(commandPanel, BorderLayout.EAST)
      _ <- inputPanel.added(rowPanel)
    } yield slider
  }

  def changeUpdatesLabelListenerAdded(slider: JSliderIO, label: JLabelIO): IO[Unit] = IO {
    slider.changeListenerAdded((event: ChangeEvent) =>
      label.textSet(event.getSource.asInstanceOf[JSlider].getValue.toString))
  }

  def clickUpdatesSliderListenerAdded(button: JButtonIO, slider: JSliderIO, checkCondition: Int => Boolean,
                                      updateFunction: Int => Int): IO[Unit] = IO {
    button.actionListenerAdded((_: ActionEvent) => for {
      currentValue <- slider.valueGot
      _ <- IO { if (checkCondition(currentValue)) slider.valueSet(updateFunction(currentValue)) }
    } yield ())
  }

  def clickCompletesEnvironmentListenerAdded(button: JButtonIO, promise: Promise[Environment],
                                             temperature: JSliderIO, luminosity: JSliderIO,
                                             initialBlobNumber: JSliderIO, initialFoodNumber: JSliderIO,
                                             initialObstacleNumber: JSliderIO, daysNumber: JSliderIO,
                                             frame: JFrame): IO[Unit] = IO {
    button.actionListenerAdded((_: ActionEvent) => for {
      t <- temperature.valueGot
      l <- luminosity.valueGot
      b <- initialBlobNumber.valueGot
      f <- initialFoodNumber.valueGot
      o <- initialObstacleNumber.valueGot
      d <- daysNumber.valueGot
      _ <- IO { promise.success(Environment(t, l, b, f, o, d)) }
    } yield ())
  }

}
