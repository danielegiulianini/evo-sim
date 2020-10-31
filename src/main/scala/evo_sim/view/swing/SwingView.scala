package evo_sim.view.swing

import java.awt.BorderLayout

import cats.effect.IO
import evo_sim.model.FinalStats.{food, population}
import evo_sim.model.World.{WorldHistory, fromIterationsToDays}
import evo_sim.model.{Environment, World}
import evo_sim.view.View
import evo_sim.view.swing.chart.ChartsFactory
import evo_sim.view.swing.custom.components.ShapesPanel
import evo_sim.view.swing.effects.InputViewEffects.inputViewCreated
import evo_sim.view.swing.monadic.{JComponentIO, JFrameIO, JLabelIO, JPanelIO}
import javax.swing.WindowConstants.EXIT_ON_CLOSE
import javax.swing._
import org.knowm.xchart.XChartPanel

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}

/** Provides a view implementation based on Swing */
object SwingView extends View {

  val frame = new JFrameIO(new JFrame("evo-sim"))

  override def inputReadFromUser(): IO[Environment] = for {
    environmentPromise <- IO pure Promise[Environment]()
    mainPanel <- inputViewCreated(environmentPromise)
    cp <- frame.contentPane()
    _ <- cp.layoutSet(new BorderLayout())
    _ <- cp.added(mainPanel, BorderLayout.CENTER)
    _ <- frame.defaultCloseOperationSet(EXIT_ON_CLOSE)
    _ <- frame.addComponentAdapterInvokingAndWaiting()
    _ <- frame.packedInvokingAndWaiting()
    _ <- frame.resizableInvokingAndWaiting(false)
    _ <- frame.visibleInvokingAndWaiting(true)
    environment <- IO(Await.result(environmentPromise.future, Duration.Inf))
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
    _ <- frame.setMaximizedExtendedStateInvokeAndWaiting()
    _ <- frame.packedInvokingAndWaiting()
  } yield ()

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

  override def resultViewBuiltAndShowed(world: WorldHistory): IO[Unit] = for {
    history <- IO { world.reverse}

    //chart <- IO { ChartsFactory.xyChart(200, 200, population(history))}
    chart <- IO { ChartsFactory.histogramChart(200, 200, population(history), food(history))}
    chartPanel <- IO { new JComponentIO(new XChartPanel(chart)) }

    cp <- frame.contentPane()
    _ <- cp.allRemovedInvokingAndWaiting()
    _ <- cp.addedInvokingAndWaiting(chartPanel, BorderLayout.CENTER)
    _ <- frame.packedInvokingAndWaiting()
    _ <- frame.visibleInvokingAndWaiting(true)
  } yield ()
}
