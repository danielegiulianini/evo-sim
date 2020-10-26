package evo_sim.view.swing.monadic

import javax.swing.BorderFactory

class BorderFactoryIO {
  def emptyBorderCreated(top: Int, left: Int, bottom: Int, right: Int): Unit = {
    BorderFactory.createEmptyBorder(top, left, bottom, right)
  }
}
