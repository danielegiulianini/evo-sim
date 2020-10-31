package evo_sim.utils

/** Counter utils */
object Counter {

  private var index = 0

  /** A counter starting from [[evo_sim.model.Utils.index]].
   *
   * @return updated counter value
   */
  def nextValue(): Int = {
    index += 1
    index
  }

}
