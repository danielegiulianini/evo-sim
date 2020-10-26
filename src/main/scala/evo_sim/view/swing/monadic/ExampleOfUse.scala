package evo_sim.view.swing.monadic

object ExampleOfUse extends App {
  val guiBuilt = for {
    jf <- JFrameIO()
    _ <- jf.titleSet("Simple example")
    _ <- jf.sizeSet(300, 200)
    _ <- jf.locationRelativeToSet(null)
    //_<- jf.defaultCloseOperationSet(JFrame.EXIT_ON_CLOSE)
  } yield jf

  val program = for {
    jf <- guiBuilt
    _<- jf.visibleInvokingAndWaiting(true)
  } yield ()

  program unsafeRunSync
}
