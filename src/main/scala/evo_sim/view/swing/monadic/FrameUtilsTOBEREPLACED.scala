package evo_sim.view.swing.monadic

import java.awt.{BorderLayout, Toolkit}

import cats.effect.IO
import javax.swing.{JButton, JComponent, JFrame, JPanel, SwingUtilities, WindowConstants}

import scala.swing.Dimension

object FrameUtilsTOBEREPLACED {

  // frame-related

  def frameCreated(title: String): IO[JFrame] = IO pure new JFrame(title)

  def allRemoved(frame: JFrame): IO[Unit] =
    IO apply SwingUtilities.invokeAndWait(() => frame.getContentPane.removeAll())

  def componentInContentPaneAdded[A <: Object](frame: JFrame, component: JPanelIO,
                                               constraint: A = BorderLayout.CENTER): IO[Unit] = for {
    _ <- IO apply SwingUtilities.invokeAndWait(() => frame.getContentPane.add(component.component, constraint))
  } yield ()

  def buttonInContentPaneAdded[A <: Object](frame: JFrame, component: JButtonIO,
                                               constraint: A = BorderLayout.CENTER): IO[Unit] = for {
    _ <- IO apply SwingUtilities.invokeAndWait(() => frame.getContentPane.add(component.component, constraint))
  } yield ()

  def exitOnCloseOperationSet(frame: JFrame): IO[Unit] =
    IO apply SwingUtilities.invokeAndWait(() => frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE))

  def isVisible(frame: JFrame): IO[Unit] = IO apply SwingUtilities.invokeAndWait(() => frame.setVisible(true))

  def isPacked(frame: JFrame): IO[Unit] = IO apply SwingUtilities.invokeAndWait(() => frame.pack())

  def isNotResizable(frame: JFrame): IO[Unit] = IO apply SwingUtilities.invokeAndWait(() => frame.setResizable(false))

  def screenSizeSet(frame: JFrame): IO[Unit] = for {
    dimension <- IO pure new Dimension(Toolkit.getDefaultToolkit.getScreenSize.width,
      Toolkit.getDefaultToolkit.getScreenSize.height)
    _ <- IO apply SwingUtilities.invokeAndWait(() => frame.setPreferredSize(dimension))
  } yield ()

}
