package evo_sim.view.swing.monadic

import scala.language.postfixOps
import java.awt.BorderLayout

import cats.effect.IO
import javax.swing.WindowConstants

object Example1 extends App {
  val guiBuilt = for {
    jf <- JFrameIO()
    _ <- jf.titleSet("Simple example")
    _ <- jf.sizeSet(300, 200)
    _ <- jf.locationRelativeToSet(null)
    _ <- jf.defaultCloseOperationSet(WindowConstants.EXIT_ON_CLOSE)
  } yield jf

  val program = for {
    jf <- guiBuilt
    _ <- jf.visibleInvokingAndWaiting(true)
  } yield ()

  program unsafeRunSync
}

object Example2 extends App {
  val frameBuilt = for {
    frame <- JFrameIO()
    _ <- frame.titleSet("example")
    _ <- frame.sizeSet(320, 200)
    _ <- frame.defaultCloseOperationSet(WindowConstants.EXIT_ON_CLOSE)
  } yield frame

  val panelBuilt = for {
    panel <- JPanelIO()
    _ <- panel.layoutSet(new BorderLayout())
    nb <- JButtonIO("North")
    _ <- panel.added(nb, BorderLayout.NORTH)
    sb <- JButtonIO("South")
    _ <- panel.added(sb, BorderLayout.SOUTH)
    eb <- JButtonIO("East")
    _ <- panel.added(eb, BorderLayout.EAST)
    wb <- JButtonIO("West (close program)")
    _ <- wb.actionListenerAddedFromUnit(System.exit(0))  //listener by procedural specification
    _ <- wb.actionListenerAdded(IO {System.exit(0)})  //listener by io monad specification
    _ <- panel.added(wb, BorderLayout.WEST)
  } yield panel

  val program = for {
    frame <- frameBuilt
    panel <- panelBuilt
    _ <- frame.added(panel)
    _ <- frame.visibleInvokingAndWaiting(true)
  } yield ()

  program unsafeRunSync
}

object Example3 extends App {

  val frameBuilt = for {
    frame <- JFrameIO()
    _ <- frame.titleSet("example")
    _ <- frame.sizeSet(320, 200)
    _ <- frame.defaultCloseOperationSet(WindowConstants.EXIT_ON_CLOSE)
  } yield frame

  val panelBuilt = for {
    panel <- JPanelIO()
    _ <- panel.layoutSet(new BorderLayout())
    l <- JLabelIO()
    sl <- JSliderIO()
    _ <- sl.changeListenerAdded(e => l.textSet(""+sl.valueGot.unsafeRunSync()).unsafeRunSync())
    _ <- panel.added(l, BorderLayout.EAST)
    _ <- panel.added(sl, BorderLayout.CENTER)
  } yield panel

  val program = for {
    frame <- frameBuilt
    panel <- panelBuilt
    _ <- frame.added(panel)
    _ <- frame.visibleInvokingAndWaiting(true)
  } yield ()

  program unsafeRunSync
}


