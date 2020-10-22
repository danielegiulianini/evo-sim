package evo_sim.prolog

import java.io.FileInputStream

import alice.tuprolog.{Prolog, Theory}

object MovingStrategiesProlog {

  def movement():Unit = {
    val engine = new Prolog()
    val theory = new Theory(new FileInputStream("file.pl"))
    engine.setTheory(theory)
    val sol = engine.solve("point(X,2).")
    println(sol)
    println(sol.getSolution)
    while(engine.hasOpenAlternatives){
      val sol = engine.solveNext()
      println("\n"+sol)
      println(sol.getSolution)
    }
  }
}
