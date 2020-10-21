import evo_sim.model.World.TrigonometricalOps.sinusoidalSin
import org.scalacheck.{Arbitrary, Prop, Properties}
import org.scalacheck.Prop.{exists, forAll}


class TestTrigonometricalOps {

  //sinusoidalSin(yTranslation: Int)(yDilatation: Float)(x:Float)(phase: Int)

  //check codomain is respected for a subset of the values
    //max = amplitude * 1 + ytranslation, min = amplitude * -1 + ytraslation

  //check periodic cycle

  object SinusoidalSpecifications extends Properties("Sinusoidal") {
    /*("max and min value respected") = forAll { (: Int, b: Float, x:Float, c) =>
      sinusoidalSin(a)(3)(3f)(0: Int)
    }*/
  }

}
