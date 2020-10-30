package evo_sim.utils

object TrigonometricalOps {

  object Sinusoidal {
    def sinusoidal(yDilatation: Float)(x: Float)(phase: Int)(yTranslation: Int): Int =
      (yDilatation * Math.sin(2 * Math.PI * x + phase)).toInt + yTranslation //should rename yDilatation to amplitude

    //most used, common and popular sinusoidalSin invocations (for this purpose translated in partially-applied functions)
    def zeroPhasedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(0)(_: Int)

    def zeroYTranslatedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(_: Int)(0)

    def oneYTranslatedSinusoidal: (Float, Float, Int) => Int = sinusoidal(_: Float)(_: Float)(_: Int)(1)

    def zeroPhasedZeroYTranslatedSinusoidal: (Float, Float) => Int = Curried.zeroPhasedSinusoidalSin(_: Float)(_: Float)(0)

    //object with curried versions to leverage, among the others, IDE automatic named parameters
    object Curried {
      def zeroPhasedSinusoidalSin: Float => Float => Int => Int = Sinusoidal.zeroPhasedSinusoidal.curried

      def zeroYTranslatedSinusoidalSin: Float => Float => Int => Int = Sinusoidal.zeroYTranslatedSinusoidal.curried

      def oneYTranslatedSinusoidalSin: Float => Float => Int => Int = Sinusoidal.oneYTranslatedSinusoidal.curried

      def zeroPhasedZeroYTranslatedSinusoidalSin: Float => Float => Int = Sinusoidal.zeroPhasedZeroYTranslatedSinusoidal.curried
    }

  }

  /*example of use:
  instead of calling: sinusoidalSin(1f)(2)(0)(1) in many places in our code, call this instead:
  zeroPhasedOneYTranslatedSinusoidalSin(1f)(2)            -------->(reuse)
  */
}
