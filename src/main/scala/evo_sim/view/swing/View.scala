package evo_sim.view.swing

import java.awt.BorderLayout

import evo_sim.model.{Constants, Environment, World}
import evo_sim.view.View
import evo_sim.view.swing.effects.InputViewEffects._
import evo_sim.view.swing.effects.SimulationViewEffects._
import evo_sim.view.swing.effects.SwingEffects._
import javax.swing._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object View extends View {

  private val frame = new JFrame("evo-sim")
  private val barPanel = new JPanel
  private val entityPanel = new JPanel

  private val userInput: Promise[Environment] = Promise[Environment]()

  override def inputViewBuiltAndShowed(): Unit = (for {
      inputPanel <- panelCreated
      _ <- verticalLayoutSet(inputPanel)
      blobSlider <- createDataInputRow(inputPanel, "#Blob", Constants.MIN_BLOBS, Constants.MAX_BLOBS,
        Constants.DEF_BLOBS)
      foodSlider <- createDataInputRow(inputPanel, "#Food", Constants.MIN_FOODS, Constants.MAX_FOODS,
        Constants.DEF_FOODS)
      obstacleSlider <- createDataInputRow(inputPanel, "#Obstacle", Constants.MIN_OBSTACLES,
        Constants.MAX_OBSTACLES, Constants.DEF_OBSTACLES)
      luminositySlider <- createDataInputRow(inputPanel, "Luminosity (cd)",
        Constants.SELECTABLE_MIN_LUMINOSITY, Constants.SELECTABLE_MAX_LUMINOSITY, Constants.DEFAULT_LUMINOSITY)
      temperatureSlider <- createDataInputRow(inputPanel, "Temperature (°C)",
        Constants.SELECTABLE_MIN_TEMPERATURE, Constants.SELECTABLE_MAX_TEMPERATURE, Constants.DEF_TEMPERATURE)
      daysSlider <- createDataInputRow(inputPanel, "#Days", Constants.MIN_DAYS, Constants.MAX_DAYS,
        Constants.DEF_DAYS)
      start <- buttonCreated("Start")
      _ <- clickCompletesEnvironmentListenerAdded(start, userInput, temperatureSlider, luminositySlider,
        blobSlider, foodSlider, obstacleSlider, daysSlider, frame)
      _ <- componentInContentPaneAdded(frame, inputPanel, BorderLayout.CENTER)
      _ <- componentInContentPaneAdded(frame, start, BorderLayout.SOUTH)
      _ <- exitOnCloseOperationSet(frame)
      _ <- isPacked(frame)
      _ <- isNotResizable(frame)
      _ <- isVisible(frame)
    } yield ()).unsafeRunSync()

  override def inputReadFromUser(): Environment =
    Await.result(userInput.future, Duration.Inf)

  override def simulationViewBuiltAndShowed(): Unit = (for {
    _ <- allRemoved(frame)
    _ <- componentInContentPaneAdded(frame, barPanel, BorderLayout.NORTH)
    _ <- componentInContentPaneAdded(frame, entityPanel, BorderLayout.CENTER)
    _ <- screenSizeSet(frame)
    _ <- isPacked(frame)
  } yield ()).unsafeRunSync()

  override def rendered(world: World): Unit = (for {
    _ <- allRemoved(entityPanel)
    // TODO statistiche
    shapes <- shapesPanelCreated(world)
    _ <- componentAdded(entityPanel, shapes)
    _ <- isPacked(frame)
  } yield ()).unsafeRunSync()

  override def resultViewBuiltAndShowed(world: World): Unit = (for {
    _ <- allRemoved(entityPanel)
    // TODO grafici
  } yield ()).unsafeRunSync()
}
