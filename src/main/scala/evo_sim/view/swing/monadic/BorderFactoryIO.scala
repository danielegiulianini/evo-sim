package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.BorderFactory
import javax.swing.border.Border

/** Factory for IO monads that describes how to create some standard [[Border]] objects.
 */
object BorderFactoryIO {
  /** Returns an [[IO]] containing the description of a [[javax.swing.BorderFactory#createEmptyBorder]]
   * method invocation.*/
  def emptyBorderCreated(top: Int, left: Int, bottom: Int, right: Int): IO[Border] = IO {
    BorderFactory.createEmptyBorder(top, left, bottom, right)
  }
}


