package evo_sim.model

object Utils {

  var index = 0

  def randomValueChange(value: Int, range: Int): Int = {
    value + new java.util.Random().nextInt(range * 2 + 1) - range
  }

  def nextValue(): Int = {
    index += 1
    index
  }

}