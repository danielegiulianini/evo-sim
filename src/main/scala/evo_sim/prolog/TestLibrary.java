package evo_sim.prolog;

import alice.tuprolog.*;
import alice.tuprolog.Double;
import alice.tuprolog.Float;
import alice.tuprolog.Number;

public class TestLibrary extends Library {

    public Term sum_2(Number arg0, Number arg1){
        float n0 = arg0.floatValue();
        float n1 = arg1.floatValue();
        return new Float(n0+n1);
    }

    public Double piGreco(){
        return new Double(Math.PI);
    }
}
