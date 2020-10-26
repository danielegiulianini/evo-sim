package evo_sim.view.swing.monadic

import java.awt.BorderLayout

object ExampleOfUse {

  object Example1 extends App {
    val guiBuilt = for {
      jf <- JFrameIO()
      _ <- jf.titleSet("Simple example")
      _ <- jf.sizeSet(300, 200)
      _ <- jf.locationRelativeToSet(null)
      //_<- jf.defaultCloseOperationSet(JFrame.EXIT_ON_CLOSE)
    } yield jf

    val program = for {
      jf <- guiBuilt
      _ <- jf.visibleInvokingAndWaiting(true)
    } yield ()

    program unsafeRunSync
  }

  object Example2 {
    val frameBuilt = for {
      frame <- JFrameIO()
      _ <- frame.titleSet("example")
      _ <- frame.sizeSet(320, 200)
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
      wb <- JButtonIO("West")
      _ <- panel.added(wb, BorderLayout.WEST)
    } yield panel

    val program2 = for {
      frame <- frameBuilt
      panel <- panelBuilt
      _ <- frame.added(panel)
      _ <- frame.visibleInvokingAndWaiting(true)
    } yield ()

    program2 unsafeRunSync
  }
}
