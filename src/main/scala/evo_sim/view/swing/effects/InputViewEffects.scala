package evo_sim.view.swing.effects

import java.awt.BorderLayout

import cats.effect.IO
import evo_sim.model.{Constants, Environment}
import evo_sim.view.swing.monadic._
import org.jfree.ui.tabbedui.VerticalLayout

import scala.concurrent.Promise

/** Provides auxiliary functions to create the input view */
object InputViewEffects {

  /** Creates a panel with components that allow to enter the simulation initial parameters
   *
   * @param environmentPromise the promise to complete with the user input
   * @return the panel to be added
   */
  def inputViewCreated(environmentPromise: Promise[Environment]): IO[JPanelIO] = for {
    inputPanel <- JPanelIO()
    _ <- inputPanel.layoutSet(new VerticalLayout())
    blobSlider <- inputRowCreated(inputPanel, "#Blob", Constants.MIN_BLOBS, Constants.MAX_BLOBS,
      Constants.DEF_BLOBS)
    plantSlider <- inputRowCreated(inputPanel, "#Plant", Constants.MIN_PLANTS, Constants.MAX_PLANTS,
      Constants.DEF_PLANTS)
    obstacleSlider <- inputRowCreated(inputPanel, "#Obstacle", Constants.MIN_OBSTACLES, Constants.MAX_OBSTACLES,
      Constants.DEF_OBSTACLES)
    luminositySlider <- inputRowCreated(inputPanel, "Luminosity (cd)", Constants.SELECTABLE_MIN_LUMINOSITY,
      Constants.SELECTABLE_MAX_LUMINOSITY, Constants.DEF_LUMINOSITY)
    temperatureSlider <- inputRowCreated(inputPanel, "Temperature (°C)", Constants.SELECTABLE_MIN_TEMPERATURE,
      Constants.SELECTABLE_MAX_TEMPERATURE, Constants.DEF_TEMPERATURE)
    daysSlider <- inputRowCreated(inputPanel, "#Days", Constants.MIN_DAYS, Constants.MAX_DAYS,
      Constants.DEF_DAYS)
    start <- JButtonIO("Start")
    _ <- start.actionListenerAdded(for {
      t <- temperatureSlider.valueGot
      l <- luminositySlider.valueGot
      b <- blobSlider.valueGot
      p <- plantSlider.valueGot
      o <- obstacleSlider.valueGot
      d <- daysSlider.valueGot
      _ <- start.enabledSet(false)
      _ <- IO { environmentPromise.success(Environment(t, l, b, p, o, d)) }
    } yield ())
    _ <- inputPanel.added(start)
  } yield inputPanel

  /* Builds a single row for a property. The row contains the name of the property with
     the current value,a slider to choose the value and buttons to adjust it */
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

  // Adds a listener to a button that changes a slider value if the condition is true
  private def clickUpdatesSliderListenerAdded(button: JButtonIO, slider: JSliderIO, checkCondition: Int => Boolean,
                                              updateFunction: Int => Int): IO[Unit] = for {
    _ <- button.actionListenerAdded(for {
      currentValue <- slider.valueGot
      _ <- if (checkCondition(currentValue)) slider.valueSet(updateFunction(currentValue)) else IO.unit
    } yield())
  } yield ()

}
