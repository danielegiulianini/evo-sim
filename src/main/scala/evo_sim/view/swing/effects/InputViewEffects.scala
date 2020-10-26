package evo_sim.view.swing.effects

import java.awt.BorderLayout

import cats.effect.IO
import evo_sim.view.swing.monadic._
import javax.swing._
import javax.swing.event.ChangeEvent

object InputViewEffects {

  def createDataInputRow(inputPanel: JPanelIO, text: String, minValue: Int, maxValue: Int,
                         defaultValue: Int): IO[JSliderIO] = {
    for {
      rowPanel <- JPanelIO()
      border <- BorderFactoryIO.emptyBorderCreated(10, 10, 10, 10)
      _ <- rowPanel.borderSet(border)
      description <- JLabelIO()
      _ <- description.textSet(text + ":")
      counter <- JLabelIO()
      _ <- counter.textSet(defaultValue.toString)
      slider <- JSliderIO()
      _ <- slider.minimumSet(minValue)
      _ <- slider.maximumSet(maxValue)
      _ <- slider.valueSet(defaultValue)
      _ <- slider.changeListenerAdded((event: ChangeEvent) =>
        (counter.textSet(event.getSource.asInstanceOf[JSlider].getValue.toString)).unsafeRunSync())
      border <- BorderFactoryIO.emptyBorderCreated(5, 0, 5, 0)
      _ <- slider.borderSet(border)
      _ <- slider.majorTickSpacingSet(maxValue / 5)
      _ <- slider.minorTickSpacingSet(1)
      _ <- slider.paintTicksSet(true)
      _ <- slider.paintLabelsSet(true)
      increment <- JButtonIO("+")
      _ <- clickUpdatesSliderListenerAdded(increment, slider, _ < maxValue, _ + 1)
      decrement <- JButtonIO("-")
      _ <- clickUpdatesSliderListenerAdded(decrement, slider, _ > minValue, _ - 1)
      infoPanel <- JPanelIO()
      _ <- infoPanel.added(description)
      _ <- infoPanel.added(counter)
      // get font size from component label
      border <- BorderFactoryIO.emptyBorderCreated((1.5 * counter.component.getFont.getSize).toInt, 0, 0, 0)
      _ <- infoPanel.borderSet(border)
      commandPanel <- JPanelIO()
      _ <- commandPanel.added(decrement)
      _ <- commandPanel.added(slider)
      _ <- commandPanel.added(increment)
      _ <- rowPanel.added(infoPanel, BorderLayout.WEST)
      _ <- rowPanel.added(commandPanel, BorderLayout.EAST)
      _ <- inputPanel.added(rowPanel)
    } yield slider
  }

  def clickUpdatesSliderListenerAdded(button: JButtonIO, slider: JSliderIO, checkCondition: Int => Boolean,
                                      updateFunction: Int => Int): IO[Unit] = IO {
    button.actionListenerAdded(_ => {
      println("click")
      val currentValue = slider.valueGot.unsafeRunSync()
      if (checkCondition(currentValue)) slider.valueSet(updateFunction(currentValue)).unsafeRunSync()
      //}) //COME ERA PRIMA TODO
    }).unsafeRunSync()
  }

}
