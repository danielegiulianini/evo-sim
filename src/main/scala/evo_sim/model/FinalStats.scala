package evo_sim.model

import scala.language.implicitConversions
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.Blob
import evo_sim.model.World.WorldHistory

object FinalStats {

  implicit def intStreamToDoubleList(stream: Stream[Int]): List[Double] = stream.map(_.toDouble).toList
  implicit def doubleStreamToDoubleList(stream: Stream[Double]): List[Double] = stream.toList

  val entityDayQuantity: (SimulableEntity => Boolean) => World => Double = (valueConsidered: SimulableEntity => Boolean) => (world: World) => entityQuantity(world, valueConsidered)
  val entityCharacteristicAverage: (SimulableEntity => Int) => World => Double = (elemToChoose: SimulableEntity => Int) => (world: World) => blobGeneticCharacteristic(world, elemToChoose)

  def days(totalIterations: Int): List[Double] = {
    (0 to totalIterations/Constants.ITERATIONS_PER_DAY).toList.map(_.toDouble)
  }

  def averageSimulation(history: WorldHistory)(valueConsidered: World => Double): Double = {
    val consideredDaysValue = dayValue(history)(valueConsidered)
    consideredDaysValue.sum / consideredDaysValue.length
  }

  def dayValue(history: WorldHistory)(valueConsidered: World => Double): List[Double] = {
    val dayHistory = history.filter(elem => (elem.currentIteration % Constants.ITERATIONS_PER_DAY equals 0) || (elem.currentIteration equals(elem.totalIterations - 1)))
    dayHistory.map(valueConsidered)
  }

  def averageDuringDay(history: WorldHistory)(iterationAverageValue: World => Double): List[Double] = {
    val origin = iterationAverageValue(history.head)
    val valueAverageDays = history.grouped(Constants.ITERATIONS_PER_DAY).map(_.map(singleIteration => iterationAverageValue(singleIteration)).sum / Constants.ITERATIONS_PER_DAY).toList
    origin :: valueAverageDays
  }

  private val blobGeneticCharacteristic: (World, SimulableEntity => Int) => Double = (world, getGeneticChar) => {
    val blobVelocity = world.entities.filter(_.isInstanceOf[Blob]).toList.map(getGeneticChar)
    blobVelocity.sum / blobVelocity.length
  }

  private val entityQuantity: (World, SimulableEntity => Boolean) => Double =  (world, elemToChoose) => world.entities.count(elemToChoose)

}
