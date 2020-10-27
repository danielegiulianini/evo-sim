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

  /** Performs a function on an object, then does it again on the result, and so on for n-times. The type of the
   * object must be the same both in input and in output
   *
   * @param times     number of times to perform the operation
   * @param initial   object on which to perform the function on
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

  /** Returns the time of the day as a float ranging from 0 to 1 excluded given the iteration number.
   * 0 is midnight, 0.5 is noon and so on.
   *
   * @param iteration iteration number
   * @return time of the day
   */
  def timeOfTheDay(iteration: Int): Float =
    iteration % Constants.ITERATIONS_PER_DAY / Constants.ITERATIONS_PER_DAY.toFloat

}