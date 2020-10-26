package evo_sim.view.swing.monadic

import javax.swing.JFrame

object ExampleOfUse extends App {
  val guiBuilt = for {
    jf <- JFrameIO()
    _ <- jf.titleSet("Simple example")
    _<- jf.sizeSet(300, 200)
    _<- jf.locationRelativeToSet(null)
    //_<- jf.defaultCloseOperationSet(JFrame.EXIT_ON_CLOSE)
    _<- jf.visibleSetInvokingAndWaiting(true)
  } yield ()

  guiBuilt.unsafeRunSync()
}
