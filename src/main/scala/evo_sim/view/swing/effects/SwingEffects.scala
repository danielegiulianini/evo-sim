package evo_sim.view.swing.effects

import java.awt.{BorderLayout, Dimension, Toolkit}

import cats.effect.IO
import javax.swing._

object SwingEffects {

  // component-related

  def borderSet[A <: JComponent](component: A, top: Int, left: Int, bottom: Int, right: Int): IO[Unit] =
    IO apply component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right))

  // frame-related

  def frameCreated(title: String): IO[JFrame] = IO pure new JFrame(title)

  def allRemoved(frame: JFrame): IO[Unit] =
    IO apply SwingUtilities.invokeAndWait(() => frame.getContentPane.removeAll())

  def componentInContentPaneAdded[A <: Object](frame: JFrame, component: JComponent,
                                               constraint: A = BorderLayout.CENTER): IO[Unit] =
    IO apply SwingUtilities.invokeAndWait(() => frame.getContentPane.add(component, constraint))

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

  // panel-related

  def panelCreated: IO[JPanel] = IO pure new JPanel

  def borderLayoutSet(panel: JPanel): IO[Unit] = IO apply panel.setLayout(new BorderLayout())

  def verticalLayoutSet(panel: JPanel): IO[Unit] = IO apply panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))

  def componentsAdded(panel: JPanel, components: JComponent*): IO[Unit] =
    IO pure components.foreach(c => panel.add(c))

  def componentAdded[A <: Object](panel: JPanel, component: JComponent,
                                  constraint: A = BorderLayout.CENTER): IO[Unit] =
    IO apply panel.add(component, constraint)

  def allRemoved(panel: JPanel): IO[Unit] = IO apply panel.removeAll()

  // label-related

  def labelCreated(text: String): IO[JLabel] = IO pure new JLabel(text)

  def textSet(label: JLabel, text: String): IO[Unit] = IO apply label.setText(text)

  // slider-related

  def sliderCreated(min: Int, max: Int, default: Int): IO[JSlider] = IO pure new JSlider(min, max, default)

  // button-related

  def buttonCreated(text: String): IO[JButton] = IO pure new JButton(text)

}
