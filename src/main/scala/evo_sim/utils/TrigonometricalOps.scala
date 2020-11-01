package evo_sim.utils

/**
 * Contains functions for performing trigonometrical operations, like sine wave computation.
 */
object TrigonometricalOps {

  /**
   * Provides sine wave computation implementations.
   * The most used and common sinusoidal invocations are translated in partially-applied functions to encourage
   * code reuse.
   */
  object Sinusoidal {
    /**
     *
     * @param yDilatation the wave amplitude
     * @param x
     * @param phase the wave phase, i.e.
     * @param yTranslation the translation applie
     * @return
     */
    def sinusoidal(yDilatation: Float)(x: Float)(phase: Int)(yTranslation: Int): Int =
      (yDilatation * Math.sin(2 * Math.PI * x + phase)).toInt + yTranslation //should rename yDilatation to amplitude

    def zeroPhasedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(0)(_: Int)

    def zeroYTranslatedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(_: Int)(0)

    def zeroPhasedZeroYTranslatedSinusoidal: (Float, Float) => Int = Curried.zeroPhasedSinusoidalSin(_: Float)(_: Float)(0)

    /**
     * Provides curried versions of [[Sinusoidal]] functions.
     * By supplyng curried versions, function invocations can leverage, among the others, IDE automatic named
     * parameters.
     */
    object Curried {
      /**
       * Curried version of [[zeroPhasedSinusoidal]] that can be invoked with a syntax like that:
       * {{{
       *   zeroPhasedSinusoidalSin(1)(2)(3)
       * }}}
       * instead of non curried version:
       * {{{
       *    zeroPhasedSinusoidalSin(1, 2, 3)
       * }}}
       *
       * @return the sine wave value corrisponding to the provided input.
       */
      def zeroPhasedSinusoidalSin: Float => Float => Int => Int = Sinusoidal.zeroPhasedSinusoidal.curried

      def zeroYTranslatedSinusoidalSin: Float => Float => Int => Int = Sinusoidal.zeroYTranslatedSinusoidal.curried

      def zeroPhasedZeroYTranslatedSinusoidalSin: Float => Float => Int = Sinusoidal.zeroPhasedZeroYTranslatedSinusoidal.curried
    }

  }

  /*example of use:
  instead of calling: sinusoidalSin(1f)(2)(0)(1) in many places in our code, call this instead:
  zeroPhasedOneYTranslatedSinusoidalSin(1f)(2)            -------->(reuse)
  */
}
