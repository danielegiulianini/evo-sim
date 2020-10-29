import evo_sim.model.Constants
import evo_sim.model.World.TrigonometricalOps.Sinusoidal.Curried._
import evo_sim.model.World.TrigonometricalOps.Sinusoidal.sinusoidal
import org.scalacheck.{Arbitrary, Prop, Properties}
import org.scalacheck.Prop.{exists, forAll}
import org.scalactic.TolerantNumerics
import org.scalatest.PropSpec
import org.scalatest.prop.Checkers


//run with: test;  from Intellij sbt shell
//or: sbt test     from cmd from the the folder cotaining build.sbt file

class SinusoidalSpecifications extends PropSpec with Checkers { //extends Properties("Sinusoidal") {
  //signature: sinusoidal(yDilatation: Float)(x:Float)(phase: Int)(yTranslation: Int)

  //1. check codomain is respected (max = amplitude * 1 + ytranslation, min = amplitude * -1 + ytraslation)
  /*property("max and min value respected") = forAll {
    (yDilatation: Float, x: Float, phase: Int, yTranslation: Int) => {
      val value = sinusoidal(yDilatation)(x)(phase)(yTranslation: Int)
      value <= yDilatation /* *1 */ + yTranslation || value >= -yDilatation /* *1 */ + yTranslation
    }
  }*/

  //2. check periodic cycle

  //3. check reversing phase sign results in reversed wave

  //4. check amplitude change results in proportional result change


  //x. check it behaves like the previous working version
  val oldYDilatation = 1 + 1 / 32f

  def oldSinusoidal(x: Int) =
    ((oldYDilatation) * Math.sin(2 * Math.PI * x / Constants.ITERATIONS_PER_DAY)).toInt


  /*property("new sinusoidal result equals old (working but not tested) sinusoidal result") {
    check {
      Prop.forAll {
        (x: Int) => {
          val newVersionValue = sinusoidalSin((oldYDilatation))(x / Constants.ITERATIONS_PER_DAY)(0)(0)
          val oldVersionValue = oldSinusoidal(x)
          println("old v."+ oldVersionValue +", new v." + newVersionValue)
          newVersionValue === oldVersionValue
        }
      }
    }
  }*/

 /* object DoubleEqualityImplicits {
    val epsilon = 1e-4f
    implicit val doubleEq = TolerantNumerics.tolerantDoubleEquality(epsilon)
  }*/
}


