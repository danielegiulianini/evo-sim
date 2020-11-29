package evo_sim.view.swing.monadic

import scala.language.postfixOps
import java.awt.BorderLayout

import cats.effect.IO
import javax.swing.{JButton, JFrame, JPanel, WindowConstants}

/**
 * This objects provides a simple example of use for the classes contained in this package that helps to build
 * GUI in a purely functional fashion.
 */
object SimpleExampleWithSwingMonadic extends App {
  val frameBuilt = for {
    frame <- JFrameIO()
    _ <- frame.titleSet("Basic GUI")
    _ <- frame.sizeSet(320, 200)
  } yield frame

  val panelBuilt = for {
    panel <- JPanelIO()
    cb <- JButtonIO("Close window.")
    _ <- cb.actionListenerAdded(System.exit(0))
    _ <- panel.added(cb)
  } yield panel

  val program = for {
    frame <- frameBuilt
    panel <- panelBuilt
    _ <- frame.added(panel)
    _ <- frame.visibleSet(true)
  } yield ()

  //thread-safer version with invoke-and-wait
  val threadSafeProgram = for {
    frame <- frameBuilt
    panel <- panelBuilt
    _ <- invokingAndWaiting(frame.added(panel))
    _ <- invokingAndWaiting(frame.visibleSet(true))
  } yield ()

  program unsafeRunSync
}

object SimpleExampleWithTraditionalSwing extends App {
  def buildFrame = {
    val frame = new JFrame
    frame.setTitle("Basic GUI")
    frame.setSize(320, 200)
    frame
  }

  def buildPanel = {
    val panel = new JPanel
    val b = new JButton("Close program)")
    b.addActionListener(_ => System.exit(0))
    panel
  }

  val panel = buildPanel
  val frame = buildFrame
  frame.getContentPane.add(panel)
  frame.setVisible(true)
}

object ExampleWithMonadicVsProceduralListeners extends App {
  val frameBuilt = for {
    frame <- JFrameIO()
    _ <- frame.titleSet("example")
    _ <- frame.sizeSet(320, 200)
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
    _ <- button.monadicActionListenerAdded(for {
      currentValue <- slider.valueGot
      _ <- if (currentValue > 0) label.textSet("value: " + currentValue)
      else IO.unit
    } yield ())
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
    case _ => println("gui building ok.")
  }
}

object ExampleWithLayoutWithSwingMonadic extends App {
  val frameBuilt = for {
    frame <- JFrameIO()
    _ <- frame.titleSet("Basic GUI")
    _ <- frame.sizeSet(320, 200)
    _ <- frame.defaultCloseOperationSet(WindowConstants.EXIT_ON_CLOSE)
  } yield frame

  val panelBuilt = for {
    panel <- JPanelIO()
    _ <- panel.layoutSet(new BorderLayout())
    nb <- JButtonIO("North")
    _ <- panel.added(nb, BorderLayout.NORTH)
    sb <- JButtonIO("Center (close program)")
    _ <- sb.actionListenerAdded(System.exit(0))
    _ <- panel.added(sb, BorderLayout.CENTER)
  } yield panel

  val program = for {
    frame <- frameBuilt
    panel <- panelBuilt
    _ <- frame.added(panel)
    _ <- frame.visibleSet(true)
  } yield ()

  program unsafeRunSync
}

object ExampleWithLayoutWithTraditionalSwing extends App {
  def buildFrame = {
    val frame = new JFrame
    frame.setTitle("Basic GUI")
    frame.setSize(320, 200)
    frame
  }

  def buildPanel = {
    val panel = new JPanel
    panel.setLayout(new BorderLayout)
    panel
  }

  val panel = buildPanel
  val frame = buildFrame
  frame.getContentPane.add(panel)
  panel.add(new JButton("North"), BorderLayout.NORTH)
  val sb = new JButton("Center (Close program)")
  sb.addActionListener(_ => System.exit(0))
  panel.add(sb, BorderLayout.CENTER)
  frame.setVisible(true)
}