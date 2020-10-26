package evo_sim.view.swing.monadic

import cats.effect.IO
import javax.swing.BorderFactory

class BorderFactoryIO {
  def emptyBorderCreated(top: Int, left: Int, bottom: Int, right: Int) = IO {
    BorderFactory.createEmptyBorder(top, left, bottom, right)
  }
}
//companion object with utilities to be added


