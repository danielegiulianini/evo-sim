package evo_sim.utils

object Function1Utils {

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

}
