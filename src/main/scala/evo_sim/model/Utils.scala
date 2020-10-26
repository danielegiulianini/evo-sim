package evo_sim.model

object Utils {

  var index = 0

  def randomValueChange(value: Int, range: Int): Int = {
    value + new java.util.Random().nextInt(range * 2 + 1) - range
  }

  def randomGender(): GenderValue.Value = {
    new java.util.Random().nextInt(3) match {
      case 0 => GenderValue.Male
      case 1 => GenderValue.Female
      case 2 => GenderValue.Genderless
    }
  }

  def nextValue(): Int = {
    index += 1
    index
  }

  /** Performs a function on an object, then does it again on the result, and so on for n-times. The type of the
   * object must be the same both in input and in output
   *
   * @param times number of times to perform the operation
   * @param initial object on which to perform the function on
   * @param operation function to use
   * @tparam B type of the initial object, and of the input and output types of the function
   * @return result object after all of the applications of the function
   */
  def chain[B](times: Int)(initial: B)(operation: B => B): B = {
    @scala.annotation.tailrec
    def f(times: Int, current: B): B = if (times > 0)
      f(times - 1, operation(current)) else current
    f(times, initial)
  }

}