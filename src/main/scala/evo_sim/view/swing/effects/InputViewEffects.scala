package evo_sim.view.swing.effects

import java.awt.BorderLayout

import cats.effect.IO
import evo_sim.model.{Constants, Environment}
import evo_sim.view.swing.monadic._
import javax.swing.WindowConstants._
import org.jfree.ui.tabbedui.VerticalLayout

import scala.concurrent.Promise

object InputViewEffects {

  def inputViewCreated(frame: JFrameIO, environmentPromise: Promise[Environment]): IO[Unit] = for {
    inputPanel <- JPanelIO()
    _ <- inputPanel.layoutSet(new VerticalLayout())
    blobSlider <- inputRowCreated(inputPanel, "#Blob", Constants.MIN_BLOBS, Constants.MAX_BLOBS,
      Constants.DEF_BLOBS)
    plantSlider <- inputRowCreated(inputPanel, "#Plant", Constants.MIN_PLANTS, Constants.MAX_PLANTS,
      Constants.DEF_PLANTS)
    obstacleSlider <- inputRowCreated(inputPanel, "#Obstacle", Constants.MIN_OBSTACLES, Constants.MAX_OBSTACLES,
      Constants.DEF_OBSTACLES)
    luminositySlider <- inputRowCreated(inputPanel, "Luminosity (cd)", Constants.SELECTABLE_MIN_LUMINOSITY,
      Constants.SELECTABLE_MAX_LUMINOSITY, Constants.DEFAULT_LUMINOSITY)
    temperatureSlider <- inputRowCreated(inputPanel, "Temperature (°C)", Constants.SELECTABLE_MIN_TEMPERATURE,
      Constants.SELECTABLE_MAX_TEMPERATURE, Constants.DEF_TEMPERATURE)
    daysSlider <- inputRowCreated(inputPanel, "#Days", Constants.MIN_DAYS, Constants.MAX_DAYS,
      Constants.DEF_DAYS)
    start <- JButtonIO("Start")
    t <- temperatureSlider.valueGot
    l <- luminositySlider.valueGot
    b <- blobSlider.valueGot
    p <- plantSlider.valueGot
    o <- obstacleSlider.valueGot
    d <- daysSlider.valueGot
    _ <- start.actionListenerAdded(IO[Unit] {
      start.enabledSet(false)
      environmentPromise.success(Environment(t, l, b, p, o, d))
    })
    cp <- frame.contentPane()
    _ <- cp.added(inputPanel, BorderLayout.CENTER)
    _ <- cp.added(start, BorderLayout.SOUTH)
    _ <- frame.defaultCloseOperationSet(EXIT_ON_CLOSE)
    _ <- frame.packedInvokingAndWaiting()
    _ <- frame.resizableInvokingAndWaiting(false)
    _ <- frame.visibleInvokingAndWaiting(true)
  } yield ()

  private def inputRowCreated(inputPanel: JPanelIO, text: String, minValue: Int, maxValue: Int,
                              defaultValue: Int): IO[JSliderIO] = for {
    rowPanel <- JPanelIO()
    _ <- rowPanel.layoutSet(new BorderLayout)
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
    _ <- slider.changeListenerAdded(for {
      value <- slider.valueGot
      _ <- counter.textSet(value.toString)
    } yield ())
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
    font <- counter.fontGot()
    border <- BorderFactoryIO.emptyBorderCreated((1.5 * font.getSize).toInt, 0, 0, 0)
    _ <- infoPanel.borderSet(border)
    commandPanel <- JPanelIO()
    _ <- commandPanel.added(decrement)
    _ <- commandPanel.added(slider)
    _ <- commandPanel.added(increment)
    _ <- rowPanel.added(infoPanel, BorderLayout.WEST)
    _ <- rowPanel.added(commandPanel, BorderLayout.EAST)
    _ <- inputPanel.added(rowPanel)
  } yield slider

  private def clickUpdatesSliderListenerAdded(button: JButtonIO, slider: JSliderIO, checkCondition: Int => Boolean,
                                              updateFunction: Int => Int): IO[Unit] = for {
    _ <- button.actionListenerAdded(for {
      currentValue <- slider.valueGot
      _ <- if (checkCondition(currentValue)) slider.valueSet(updateFunction(currentValue)) else IO.unit
    } yield())
  } yield ()

}
