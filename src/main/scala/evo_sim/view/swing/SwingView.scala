package evo_sim.view.swing

import java.awt.{BorderLayout, GraphicsEnvironment}

import cats.effect.IO
import evo_sim.model.EntityStructure.{Blob, Food, Obstacle}
import evo_sim.model.FinalStats.{averageDuringDay, averageSimulation, dayValue, days, entityCharacteristicAverage, entityDayQuantity}
import evo_sim.model.World.{WorldHistory, fromIterationsToDays}
import evo_sim.model.{Constants, Environment, World}
import evo_sim.view.View
import evo_sim.view.swing.SwingView.ViewUtils.InputViewUtils.inputViewCreated
import evo_sim.view.swing.SwingView.ViewUtils.ResultViewUtils
import evo_sim.view.swing.SwingView.ViewUtils.SimulationViewUtils.indicatorsUpdated
import evo_sim.view.swing.chart.{CategorySeries, ChartsBuilder, PieValue, XySeries}
import evo_sim.view.swing.custom.components.ShapesPanel
import evo_sim.view.swing.monadic._
import javax.swing.WindowConstants.EXIT_ON_CLOSE
import javax.swing._
import org.jfree.ui.tabbedui.VerticalLayout
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle
import org.knowm.xchart.XChartPanel
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.style.markers.SeriesMarkers

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
    windowsBounds <- IO pure GraphicsEnvironment.getLocalGraphicsEnvironment.getMaximumWindowBounds
    _ <- frame.sizeSet(windowsBounds.width, windowsBounds.height)
    _ <- frame.packedInvokingAndWaiting()
  } yield ()

  override def resultsShowed(world: WorldHistory): IO[Unit] = for {

    panel <- ResultViewUtils.panelWithChart(world.reverse)

    cp <- frame.contentPane()
    _ <- cp.allRemovedInvokingAndWaiting()
    _ <- cp.added(panel)
    _ <- frame.packedInvokingAndWaiting()
    _ <- frame.visibleInvokingAndWaiting(true)
  } yield ()

  /** Contains some utilities for creating [[SwingView]].*/
  object ViewUtils {
    object InputViewUtils {
      def inputViewCreated(environmentPromise: Promise[Environment]): IO[JPanelIO] = for {
        mainPanel <- JPanelIO()
        _ <- mainPanel.layoutSet(new BorderLayout())
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
        _ <- mainPanel.added(inputPanel, BorderLayout.CENTER)
        _ <- mainPanel.added(start, BorderLayout.SOUTH)
      } yield mainPanel

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

    object SimulationViewUtils {
      def indicatorsUpdated(world:World, barPanel:JPanelIO): IO[Unit] = {
        def jLabelWithItemsAddedToJPanel[T](jPanel: JPanelIO)(text: String) =
          for {
            jl <- JLabelIO()
            _ <- jl.textSet(text)
            _ <- jPanel.added(jl)
          } yield ()

        def jLabelWithItemsAddedToBarPanel =
          jLabelWithItemsAddedToJPanel(barPanel)(_)

        for {
          _ <- barPanel.allRemoved()      //barPanel has default FlowLayout
          _ <- jLabelWithItemsAddedToBarPanel("days: " + fromIterationsToDays(world.currentIteration) + " / " + fromIterationsToDays(world.totalIterations))
          _ <- jLabelWithItemsAddedToBarPanel("population: " + world.entities.collect{case b: Blob => b}.size)
          _ <- jLabelWithItemsAddedToBarPanel("luminosity: " + world.luminosity)
        } yield ()
      }
    }

    object ResultViewUtils {

      def panelWithChart(history: WorldHistory): IO[JPanelIO] = for {

        days <- IO { days(history.head.totalIterations) }

        velocity <- IO {
        XySeries("Velocity",
        days,
        averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].velocity)),
        SeriesMarkers.NONE,
        XYSeriesRenderStyle.Line)}

        dimension <- IO {
        XySeries("Dimension",
        days,
        averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].boundingBox.radius)),
        SeriesMarkers.NONE,
        XYSeriesRenderStyle.Line)}

        fov <- IO {
        XySeries("Field of View",
        days,
        averageDuringDay(history)(entityCharacteristicAverage(_.asInstanceOf[Blob].fieldOfViewRadius)),
        SeriesMarkers.NONE,
        XYSeriesRenderStyle.Line)}

        population <- IO {
        CategorySeries("Population",
        days,
        dayValue(history)(entityDayQuantity(_.isInstanceOf[Blob])),
        SeriesMarkers.NONE,
        CategorySeriesRenderStyle.Bar)}

        food <- IO {
        CategorySeries("Food",
        days,
        dayValue(history)(entityDayQuantity(_.isInstanceOf[Food])),
        SeriesMarkers.NONE,
        CategorySeriesRenderStyle.Line)}

        simulationBlobPercentage <- IO { PieValue("Base", population.yData.sum/population.yData.length) }
        simulationFoodPercentage <- IO { PieValue("Food", food.yData.sum/population.yData.length) }
        simulationObstaclePercentage <- IO { PieValue("Obstacle", averageSimulation(history)(entityDayQuantity(_.isInstanceOf[Obstacle]))) }

        populationChart <- IO { ChartsBuilder.histogramChart("Population", 675, 300, population, food)}
        populationChartPanel <- IO { new JComponentIO(new XChartPanel(populationChart)) }

        velocityChart <- IO { ChartsBuilder.xyChart("Entities characteristic average",675, 300, velocity, dimension, fov) }
        velocityChartPanel <- IO { new JComponentIO(new XChartPanel(velocityChart)) }

        typologyChart <- IO { ChartsBuilder.pieChart("Simulation entities percentage", 400, 200, simulationBlobPercentage, simulationFoodPercentage, simulationObstaclePercentage)}
        typologyChartPanel <- IO { new JComponentIO(new XChartPanel(typologyChart)) }

        panel <- JPanelIO()
        _ <- panel.added(populationChartPanel)
        _ <- panel.added(velocityChartPanel)
        _ <- panel.added(typologyChartPanel)

      } yield panel
    }
  }
}
