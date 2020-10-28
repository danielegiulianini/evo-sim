package evo_sim.view.swing

import java.awt.{BorderLayout, Dimension, Toolkit}
import java.awt.event.ActionEvent

import cats.effect.IO
import evo_sim.model.{Constants, Environment, World}
import evo_sim.view.View
import evo_sim.view.swing.effects.InputViewEffects._
import evo_sim.view.swing.monadic.{JButtonIO, JFrameIO, JPanelIO}
import javax.swing._
import org.jfree.ui.tabbedui.VerticalLayout

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import evo_sim.view.swing.monadic.FrameUtilsTOBEREPLACED._

object View extends View {

  private val frame = new JFrame("evo-sim")

  lazy val frameEncapsulated = new JFrameIO(frame)

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
      //    } yield ()) //as was before
    } yield ()).unsafeRunSync)
    cp <- frameEncapsulated.contentPane
    _<- cp.added(inputPanel, BorderLayout.CENTER)
    _<- cp.added(start, BorderLayout.SOUTH)
    _ <- frameEncapsulated.defaultCloseOperationSet(WindowConstants.EXIT_ON_CLOSE)
    _ <- frameEncapsulated.packedInvokingAndWaiting()
    _ <- frameEncapsulated.resizableInvokingAndWaiting(false)
    _ <- frameEncapsulated.visibleInvokingAndWaiting(true)

    /* as it was before:
    _ <- componentInContentPaneAdded(frame, inputPanel, BorderLayout.CENTER)
    _ <- buttonInContentPaneAdded(frame, start, BorderLayout.SOUTH)
    _ <- exitOnCloseOperationSet(frame)
    _ <- isPacked(frame)
    _ <- isNotResizable(frame)
    _ <- isVisible(frame)
    */

    environment <- IO {
      Await.result(environmentPromise.future, Duration.Inf)
    }
  } yield environment

  override def rendered(world: World): IO[Unit] = for {
    barPanel <- JPanelIO()
    entityPanel <- JPanelIO()
    // TODO statistiche
    shapes <- IO {
      new JPanelIO(new ShapesPanel(world))
    }
    _ <- entityPanel.added(shapes)

    cp <- frameEncapsulated.contentPane()
    _<- cp.allRemovedInvokingAndWaiting()
    _<- cp.addedInvokingAndWaiting(barPanel, BorderLayout.NORTH)
    _<- cp.addedInvokingAndWaiting(entityPanel, BorderLayout.CENTER)
    _ <- frameEncapsulated.packedInvokingAndWaiting()
    _ <- frameEncapsulated.visibleInvokingAndWaiting(true)
    dimension <- IO {
      new Dimension(Toolkit.getDefaultToolkit.getScreenSize.width,
      Toolkit.getDefaultToolkit.getScreenSize.height)
    }
    _<- frameEncapsulated.setPreferredSizeInvokingAndWaiting(dimension)
    _ <- frameEncapsulated.packedInvokingAndWaiting()

    /*_ <- allRemoved(frame)
    _ <- componentInContentPaneAdded(frame, barPanel, BorderLayout.NORTH)
    _ <- componentInContentPaneAdded(frame, entityPanel, BorderLayout.CENTER)
    _ <- screenSizeSet(frame)
    _ <- isPacked(frame)*/
  } yield ()

  override def resultViewBuiltAndShowed(world: World): IO[Unit] = for {
    _ <- IO {}
    // TODO grafici
  } yield ()
}
