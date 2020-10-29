package evo_sim.view.swing

import java.awt.{BorderLayout, Container, Dimension, Toolkit}
import java.awt.event.ActionEvent

import cats.effect.IO
import evo_sim.model.Constants.ITERATIONS_PER_DAY
import evo_sim.model.World.fromIterationsToDays
import evo_sim.model.{Constants, Environment, World}
import evo_sim.view.View
import evo_sim.view.swing.chart.JFreeChart.{JFreeChart, XYSeries, XYSeriesCollection}
import evo_sim.view.swing.chart.View.{setContentPane, setDefaultCloseOperation, setSize, setVisible}
import evo_sim.view.swing.custom.components.ShapesPanel
import evo_sim.view.swing.effects.InputViewEffects._
import evo_sim.view.swing.monadic.{JButtonIO, JFrameIO, JLabelIO, JPanelIO}
import javax.swing._
import org.jfree.chart.ChartFactory
import org.jfree.ui.tabbedui.VerticalLayout
import org.knowm.xchart.{XChartPanel, XYChartBuilder}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

object View extends View {

  val frame = new JFrameIO(new JFrame("evo-sim"))

  override def inputReadFromUser(): IO[Environment] = for {
    environmentPromise <- IO pure {
      Promise[Environment]()
    }
    inputPanel <- JPanelIO()
    _ <- inputPanel.layoutSet(new VerticalLayout())
    blobSlider <- createDataInputRow(inputPanel, "#Blob", Constants.MIN_BLOBS, Constants.MAX_BLOBS,
      Constants.DEF_BLOBS)
    plantSlider <- createDataInputRow(inputPanel, "#Plant", Constants.MIN_PLANTS, Constants.MAX_PLANTS,
      Constants.DEF_PLANTS)
    obstacleSlider <- createDataInputRow(inputPanel, "#Obstacle", Constants.MIN_OBSTACLES,
      Constants.MAX_OBSTACLES, Constants.DEF_OBSTACLES)
    luminositySlider <- createDataInputRow(inputPanel, "Luminosity (cd)",
      Constants.SELECTABLE_MIN_LUMINOSITY, Constants.SELECTABLE_MAX_LUMINOSITY, Constants.DEFAULT_LUMINOSITY)
    temperatureSlider <- createDataInputRow(inputPanel, "Temperature (°C)",
      Constants.SELECTABLE_MIN_TEMPERATURE, Constants.SELECTABLE_MAX_TEMPERATURE, Constants.DEF_TEMPERATURE)
    daysSlider <- createDataInputRow(inputPanel, "#Days", Constants.MIN_DAYS, Constants.MAX_DAYS,
      Constants.DEF_DAYS)
    start <- JButtonIO("Start")
    _ <- start.actionListenerAdded((_: ActionEvent) => (for {
      _ <- start.enabledSet(false)
      t <- temperatureSlider.valueGot
      l <- luminositySlider.valueGot
      b <- blobSlider.valueGot
      p <- plantSlider.valueGot
      o <- obstacleSlider.valueGot
      d <- daysSlider.valueGot
      _ <- IO {
        environmentPromise.success(Environment(t, l, b, p, o, d))
      }
    } yield ()).unsafeRunSync)
    cp <- frame.contentPane()
    _ <- cp.added(inputPanel, BorderLayout.CENTER)
    _ <- cp.added(start, BorderLayout.SOUTH)
    _ <- frame.defaultCloseOperationSet(WindowConstants.EXIT_ON_CLOSE)
    _ <- frame.packedInvokingAndWaiting()
    _ <- frame.resizableInvokingAndWaiting(false)
    _ <- frame.visibleInvokingAndWaiting(true)
    environment <- IO {
      Await.result(environmentPromise.future, Duration.Inf)
    }
    _ <- frame.addComponentAdapterInvokingAndWaiting()
    dimension <- IO {
      new Dimension(Toolkit.getDefaultToolkit.getScreenSize.width,
        Toolkit.getDefaultToolkit.getScreenSize.height)
    }
    _ <- frame.setPreferredSizeInvokingAndWaiting(dimension)
    _ <- frame.resizableInvokingAndWaiting(true)
  } yield environment

  override def rendered(world: World): IO[Unit] = for {
    barPanel <- JPanelIO()
    entityPanel <- JPanelIO()
    _ <- indicatorsUpdated(world, barPanel)
    shapes <- IO { new JPanelIO(new ShapesPanel(world)) }
    _ <- entityPanel.added(shapes)
    cp <- frame.contentPane()
    _ <- cp.allRemovedInvokingAndWaiting()
    _ <- cp.addedInvokingAndWaiting(barPanel, BorderLayout.NORTH)
    _ <- cp.addedInvokingAndWaiting(entityPanel, BorderLayout.CENTER)
    _ <- frame.packedInvokingAndWaiting()
  } yield ()

  //inside rendered
  private def indicatorsUpdated(world:World, barPanel:JPanelIO): IO[Unit] = {
    for {
      //barPanel has default FlowLayout
      _ <- barPanel.allRemoved()
      days <- JLabelIO("days: " + fromIterationsToDays(world.currentIteration) + " / " + fromIterationsToDays(world.totalIterations))
      _ <- barPanel.added(days)
      population <- JLabelIO("population: " + world.entities.size)
      _ <- barPanel.added(population)
      population <- JLabelIO("temp: " + world.temperature)
      _ <- barPanel.added(population)
      population <- JLabelIO("luminosity: " + world.luminosity)
      _ <- barPanel.added(population)
    } yield ()
    //refactor repeated code for jlabels
  }

  override def resultViewBuiltAndShowed(world: World): IO[Unit] = for {
    _ <- IO {}
    // TODO grafici
  } yield ()
}
