package evo_sim.prolog

import alice.tuprolog._
import evo_sim.model.Constants.{ITERACTION_LAPSE, MAX_STEP_FOR_ONE_DIRECTION, WORLD_HEIGHT, WORLD_WIDTH}
import evo_sim.model.EntityStructure.Intelligent
import evo_sim.model.{Direction, Movement, Point2D}
import evo_sim.prolog.PrologEngine.engine


object MovingStrategiesProlog {

  implicit def termToInt(t:Term): scala.Int = t.toString.toInt

  implicit def toStructPoint(point: Point2D): Term = new Struct("point", new Int(point.x), new Int(point.y))

  implicit def toPrologInt(value: scala.Int): Int = new Int(value)

  implicit def toPrologDouble(value: scala.Double): Double = new Double(value)

  def standardMovement(entity: Intelligent): Movement = {
    val goal: Term = new Struct("standardMov", entity.boundingBox.point, entity.velocity,
                                  entity.direction.angle, entity.direction.stepToNextDirection, constantTerm,
                                  new Var("Point"), new Var("Direction"))

    val solution = engine(goal)
    val nextPosition = solution.iterator.next()

    Movement(Point2D(extractVarValue(nextPosition,"Point", 0),extractVarValue(nextPosition,"Point", 1)),
              Direction(extractVarValue(nextPosition,"Direction", 0),extractVarValue(nextPosition, "Direction", 1)))

  }

  def chaseMovement(entity: Intelligent, chasedEntity: Point2D): Movement = {
    val goal: Term = new Struct("chaseMov", entity.boundingBox.point, chasedEntity, entity.velocity, constantTerm,
                                 new Var("Point"), new Var("Direction"))

    val solution = engine(goal)
    val nextPosition = solution.iterator.next()

    Movement(Point2D(extractVarValue(nextPosition,"Point", 0),extractVarValue(nextPosition,"Point", 1)),
      Direction(extractVarValue(nextPosition,"Direction", 0),extractVarValue(nextPosition, "Direction", 1)))
  }

  private def extractVarValue(solveInfo: SolveInfo, varName: String, argNumber: scala.Int): Term =
    solveInfo.getVarValue(varName).asInstanceOf[Struct].getArg(argNumber)

  private def constantTerm: Term = new Struct("simulationConstants", scala.math.Pi, MAX_STEP_FOR_ONE_DIRECTION, WORLD_WIDTH, WORLD_HEIGHT, ITERACTION_LAPSE)

}
