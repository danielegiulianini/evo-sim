package evo_sim.model.entities.entityStructure.movement

import alice.tuprolog.{Double, Int, Struct, Term, Var}
import evo_sim.model.entities.entityBehaviour.EntityBehaviour.SimulableEntity
import evo_sim.model.entities.entityStructure.EntityStructure.{Entity, Intelligent}
import evo_sim.model.entities.entityStructure.Point2D
import evo_sim.model.world.Constants.{ITERATION_LAPSE, MAX_STEP_FOR_ONE_DIRECTION}
import evo_sim.model.world.World
import evo_sim.prolog.PrologEngine.engine

object DemonstrationCompletelyPrologMovement {

  /** Implicit to convert a [[Term]] into a [[scala.Int]].
   *
   * @param t the term to convert.
   * @return the equivalent [[scala.Int]] value of the [[Term]].
   */
  implicit def termToInt(t: Term): scala.Int = t.toString.toInt

  /** Implicit to convert a [[scala.Int]] into a Prolog [[Int]].
   *
   * @param value the [[scala.Int]] to convert.
   * @return the Prolog [[Int]] equivalent at the [[scala.Int]].
   */
  implicit def toPrologInt(value: scala.Int): Int = new Int(value)

  /** Implicit to convert a [[scala.Double]] into a Prolog [[Double]].
   *
   * @param value the [[scala.Double]] to convert.
   * @return the Prolog [[Double]] equivalent at the [[scala.Double]].
   */
  implicit def toPrologDouble(value: scala.Double): Double = new Double(value)

  /** Implicit to convert a [[Point2D]] to a [[Term]].
   *
   * @param point the [[Point2D]] to convert.
   * @return the [[Term]] equivalent at the [[Point2D]].
   */
  implicit def toStructPoint(point: Point2D): Term = new Struct("point", new Int(point.x), new Int(point.y))

  /** Implicit to convert a [[Term]] to a [[Point2D]].
   *
   * @param t the [[Term]] to convert.
   * @return the [[Point2D]] equivalent at the [[Term]].
   */
  implicit def termToPoint2D(t: Term): Point2D = Point2D(extractVarValue(t, 0), extractVarValue(t, 1))

  /** Implicit to convert a [[Term]] to a [[Direction]].
   *
   * @param t the [[Term]] to convert.
   * @return the [[Direction]] equivalent at the [[Term]].
   */
  implicit def termToDirection(t: Term): Direction = Direction(extractVarValue(t, 0), extractVarValue(t, 1))

  /** Equivalent to [[MovingStrategies.baseMovement]] but fully implemented in Prolog.
   *
   *  @param entity to be moved.
   *  @param world containing all the simulation information.
   *  @param entitiesFilter that describes all possible eatable entities.
   *  @return a Movement that contains the new position and the new direction.
   */
  def completelyPrologBaseMovement(entity: Intelligent, world: World, entitiesFilter: Entity => Boolean): Movement = {

    val entitiesPointTerm = (world.entities - entity.asInstanceOf[SimulableEntity])
      .filter(entitiesFilter)
      .map(eatableEntity => toStructPoint(eatableEntity.boundingBox.point))
      .fold(new Struct())((entitiesTermList, entity) => new Struct(entity, entitiesTermList))

    /* Term containing the constant values needed to calculate the new position */
    val simulationConstantTerm: Term = new Struct("simulationConstants", scala.math.Pi, MAX_STEP_FOR_ONE_DIRECTION, world.width, world.height, ITERATION_LAPSE)

    val pointVal = new Var("Point")
    val directionVal = new Var("Direction")

    val movingValue = s"movingValue(${entity.velocity}, ${entity.direction.angle}, ${entity.direction.stepToNextDirection}, ${entity.fieldOfViewRadius})"

    val goal = new Struct("baseMovement", entity.boundingBox.point, Term.createTerm(movingValue), simulationConstantTerm,
      entitiesPointTerm, pointVal, directionVal)

    val solution = engine(goal)
    val solveInfo = solution.iterator.next()

    Movement(solveInfo.getVarValue(pointVal.getName), solveInfo.getVarValue(directionVal.getName))
  }

  /** Gets the i-th [[Term]] contained in the [[Term]] passed as a parameter.
   *
   * @param term      the term whence extract the information.
   * @param argNumber the index of the argument to extract.
   * @return the i-th [[Term]] extracted.
   */
  private def extractVarValue(term: Term, argNumber: scala.Int): Term = term.asInstanceOf[Struct].getArg(argNumber)

}
