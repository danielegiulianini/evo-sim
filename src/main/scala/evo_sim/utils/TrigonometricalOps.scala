package evo_sim.utils

/**
 * Contains functions for performing trigonometrical operations, like sine wave computation.
 */
object TrigonometricalOps {

  /**
   * Provides sine wave computation implementations.
   * The most used and common sinusoidal invocations are translated into partially-applied functions to encourage
   * code reuse.
   */
  object Sinusoidal {
    /**
     * Computes the trigonometrical sinusoidal function for the input provided.
     * If the argument is NaN or an infinity, then the result is NaN.
     * If the argument is zero, then the result is a zero with the same sign as the argument.
     * @param yDilatation the wave amplitude, ie. the max deviation from zero before applying yTranslation
     * @param x the domain value where the function needs to be computed
     * @param phase the wave phase, i.e. the value of the sine is when x would be 0 (it causes horizontal translation)
     * @param yTranslation the translation applied to the y value after computing the A * sin (...) product
     * @return the value corresponding to input provided.
     */
    def sinusoidal(yDilatation: Float)(x: Float)(phase: Int)(yTranslation: Int): Int =
      (yDilatation * Math.sin(2 * Math.PI * x + phase)).toInt + yTranslation //should rename yDilatation to amplitude

    def zeroPhasedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(0)(_: Int)

    def zeroYTranslatedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(_: Int)(0)

    def zeroPhasedZeroYTranslatedSinusoidal: (Float, Float) => Int = Curried.zeroPhasedSinusoidal(_: Float)(_: Float)(0)

    /**
     * Provides curried versions of [[Sinusoidal]] functions.
     * By supplying curried versions, function invocations can leverage, among the others, IDE automatic named
     * parameters and can be partially applied, in their turn.
     */
    object Curried {
      /**
       * Curried version of [[zeroPhasedSinusoidal]] that can be invoked with a syntax like that:
       * {{{
       *   zeroPhasedSinusoidal(1)(2)(3)
       * }}}
       * instead of non curried version:
       * {{{
       *    zeroPhasedSinusoidal(1, 2, 3)
       * }}}
       *
       * @return the sine wave value corresponding to the provided input.
       */
      def zeroPhasedSinusoidal: Float => Float => Int => Int = Sinusoidal.zeroPhasedSinusoidal.curried

      /**
       * Curried version of [[zeroYTranslatedSinusoidal]] that can be invoked with a syntax like that:
       * {{{
       *   zeroYTranslatedSinusoidal(1)(2)(3)
       * }}}
       * instead of non curried version:
       * {{{
       *    zeroYTranslatedSinusoidal(1, 2, 3)
       * }}}
       *
       * @return the sine wave value corresponding to the provided input.
       */
      def zeroYTranslatedSinusoidal: Float => Float => Int => Int = Sinusoidal.zeroYTranslatedSinusoidal.curried

      /**
       * Curried version of [[zeroPhasedZeroYTranslatedSinusoidal]] that can be invoked with a syntax like that:
       * {{{
       *   zeroPhasedZeroYTranslatedSinusoidal(1)(2)
       * }}}
       * instead of non curried version:
       * {{{
       *    zeroPhasedZeroYTranslatedSinusoidal(1, 2)
       * }}}
       *
       * @return the sine wave value corresponding to the provided input.
       */
      def zeroPhasedZeroYTranslatedSinusoidal: Float => Float => Int = Sinusoidal.zeroPhasedZeroYTranslatedSinusoidal.curried
    }

  }

  /*example of use:
  instead of calling: sinusoidalSin(1f)(2)(0)(1) in many places in our code, call this instead:
  zeroPhasedOneYTranslatedSinusoidalSin(1f)(2)            -------->(reuse)
  */
}
