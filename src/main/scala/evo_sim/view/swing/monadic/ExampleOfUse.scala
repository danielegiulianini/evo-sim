package evo_sim.view.swing.monadic

import scala.language.postfixOps
import java.awt.BorderLayout

import cats.effect.IO
import javax.swing.{JButton, JFrame, JPanel, WindowConstants}

/** Contains a simple example of use for building a Basic GUI with the classes contained in this package. */
object SimpleExampleWithSwingMonadic extends App {
  val frameBuilt = for {
    frame <- JFrameIO()
    _ <- frame.titleSet("Basic GUI")
    _ <- frame.sizeSet(320, 200)
  } yield frame

  val panelBuilt = for {
    panel <- JPanelIO()
    cb <- JButtonIO("Close program.")
    _ <- cb.actionListenerAdded(System.exit(0))
    _ <- panel.added(cb)
  } yield panel

  val program = for {
    frame <- frameBuilt
    panel <- panelBuilt
    _ <- frame.added(panel)
    _ <- frame.visibleSet(true)
  } yield ()

  program unsafeRunSync
}

/** Contains the procedural/OO-equivalent code of [[SimpleExampleWithSwingMonadic]] leveraging traditional
 * swing APIs. */
object SimpleExampleWithTraditionalSwing extends App {
  def buildFrame = {
    val frame = new JFrame
    frame.setTitle("Basic GUI")
    frame.setSize(320, 200)
    frame
  }

  def buildPanel = {
    val panel = new JPanel
    val b = new JButton("Close program")
    b.addActionListener(_ => System.exit(0))
    panel.add(b)
    panel
  }

  val panel = buildPanel
  val frame = buildFrame
  frame.add(panel)
  frame.setVisible(true)
}

/** Contains a simple example of a GUI with a comparison of procedural vs monadic
 * action listener specification. */
object ExampleWithMonadicVsProceduralListeners extends App {
  val frameBuilt = for {
    frame <- JFrameIO()
    _ <- frame.titleSet("Basic GUI with listeners")
    _ <- frame.sizeSet(400, 400)
    _ <- frame.resizableSet(true)
    _ <- frame.defaultCloseOperationSet(WindowConstants.EXIT_ON_CLOSE)
  } yield frame

  val panelBuilt = for {
    panel <- JPanelIO()
    _ <- panel.layoutSet(new BorderLayout())
    slider <- JSliderIO()
    _ <- panel.added(slider, BorderLayout.CENTER)
    label <- JLabelIO("value: ?")
    _ <- panel.added(label, BorderLayout.NORTH)
    button <- JButtonIO("Click to display positive value.")
    //monadic listener:
    _ <- button.monadicActionListenerAdded(for {
      currentValue <- slider.valueGot
      _ <- if (currentValue > 0) label.textSet("value: " + currentValue)
      else IO.unit
    } yield ())
    //procedural listener:
    _ <- button.actionListenerAdded(System.out.println("button pressed"))
    _ <- panel.added(button, BorderLayout.SOUTH)
  } yield panel

  val program = for {
    frame <- frameBuilt
    panel <- panelBuilt
    _ <- frame.added(panel)
    _ <- frame.visibleSet(true)
  } yield ()

  //example of execution with unsafeRunAsync (async, callback-based API)
  program unsafeRunAsync {
    case Left(_) => println("an exception was raised during gui-building.")
    case _ => println("gui-building ok.")
  }
}

/** Contains a simple example of use of the classes of this package for building a frame with explicit use
 * of layout managers in a thread-safe manner. */
object ThreadSafeExampleWithLayoutWithSwingMonadic extends App {
  val threadSafelyFrameBuilt = for {
    frame <- JFrameIO()
    _ <- invokingAndWaiting(for {
      _ <- frame.titleSet("Basic GUI with layouts")
      _ <- frame.sizeSet(320, 200)
      _ <- frame.defaultCloseOperationSet(WindowConstants.EXIT_ON_CLOSE)
    } yield ())
  } yield frame

  val threadSafelyPanelBuilt = for {
    panel <- JPanelIO()
    _ <- invokingAndWaiting(
      for {
        _ <- panel.layoutSet (new BorderLayout ())
        nb <- JButtonIO ("North")
        _ <- panel.added (nb, BorderLayout.NORTH)
        wb <- JButtonIO ("East")
        _ <- panel.added (wb, BorderLayout.WEST)
        eb <- JButtonIO ("East")
        _ <- panel.added (eb, BorderLayout.EAST)
        sb <- JButtonIO ("South")
        _ <- panel.added (sb, BorderLayout.SOUTH)
        cb <- JButtonIO ("Center (close program)")
        _ <- cb.actionListenerAdded (System.exit (0))
        _ <- panel.added (cb, BorderLayout.CENTER)
      } yield ())
  } yield panel

  val threadSafeProgram = for {
    frame <- threadSafelyFrameBuilt
    panel <- threadSafelyPanelBuilt
    _ <- invokingAndWaiting(frame.added(panel))
    _ <- invokingAndWaiting(frame.visibleSet(true))
  } yield ()

  threadSafeProgram unsafeRunSync
}