import evo_sim.model.World.TrigonometricalOps.Curried._
import evo_sim.model.World.TrigonometricalOps.sinusoidalSin
import org.scalacheck.{Arbitrary, Prop, Properties}
import org.scalacheck.Prop.{exists, forAll}


class TestTrigonometricalOps {

  //signature: sinusoidalSin(yDilatation: Float)(x:Float)(phase: Int)(yTranslation: Int)

  //1. check codomain is respected for a subset of the values (max = amplitude * 1 + ytranslation, min = amplitude * -1 + ytraslation)
  
  object SinusoidalSpecifications extends Properties("Sinusoidal") {
      property("max and min value respected") = forAll { (yDilatation: Float, x: Float, phase: Int, yTranslation: Int) => {
        val value = sinusoidalSin(yDilatation)(x)(phase)(yTranslation: Int)
        value <= yDilatation /* *1 */ + yTranslation && value <= -yDilatation /* *1 */ + yTranslation
      }
    }
  }


  //2. check periodic cycle

}
