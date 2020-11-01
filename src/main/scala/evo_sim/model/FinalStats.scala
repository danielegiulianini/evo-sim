package evo_sim.model

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.{Blob, Food}
import evo_sim.model.World.WorldHistory

object FinalStats {

  implicit def intStreamToDoubleList(stream: Stream[Int]): List[Double] = stream.map(_.toDouble).toList

  implicit def doubleStreamToDoubleList(stream: Stream[Double]): List[Double] = stream.toList

  /*def population(history: WorldHistory): List[Double] = {
    val dayInformation = history.filter(elem => (elem.currentIteration % Constants.ITERATIONS_PER_DAY equals 0) || (elem.currentIteration equals(elem.totalIterations - 1)))
    dayInformation.map(_.entities.count(_.isInstanceOf[Blob]))
  }

  def food(history: WorldHistory): List[Double] = {
    val dayInformation = history.filter(elem => (elem.currentIteration % Constants.ITERATIONS_PER_DAY equals 0) || (elem.currentIteration equals(elem.totalIterations - 1)))
    dayInformation.map(_.entities.count(_.isInstanceOf[Food]))
  }*/

  def averageDayEnd(history: WorldHistory)(dayAverageValue: World => Double): List[Double] = {
    val dayHistory = history.filter(elem => (elem.currentIteration % Constants.ITERATIONS_PER_DAY equals 0) || (elem.currentIteration equals(elem.totalIterations - 1)))
    dayHistory.map(dayAverageValue)
  }

  val blobQuantity: World => Double = (world: World) => entityQuantity(world, _.isInstanceOf[Blob])
  val foodQuantity: World => Double = (world: World) => entityQuantity(world, _.isInstanceOf[Food])

  private val entityQuantity: (World, SimulableEntity => Boolean) => Double =  (world, elemToChoose) => world.entities.count(elemToChoose)

  def averageDuringDay(history: WorldHistory)(iterationAverageValue: Set[SimulableEntity] => Double): List[Double] = {
    val origin = iterationAverageValue(history.head.entities)
    val valueAverageDays = history.grouped(Constants.ITERATIONS_PER_DAY).map(_.map(singleIteration => iterationAverageValue(singleIteration.entities)).sum / Constants.ITERATIONS_PER_DAY).toList
    origin :: valueAverageDays
  }

  val velocityAverage: Set[SimulableEntity] => Double = (entities: Set[SimulableEntity]) => {
    /* val blobVelocity = entities.filter(_.isInstanceOf[Blob]).toList.map(_.asInstanceOf[Blob].velocity)
    blobVelocity.sum / blobVelocity.length */
    blobGeneticCharacteristic(entities, _.asInstanceOf[Blob].velocity)
  }

  val dimensionAverage: Set[SimulableEntity] => Double = (entities: Set[SimulableEntity]) => {
    /* val blobVelocity = entities.filter(_.isInstanceOf[Blob]).toList.map(_.asInstanceOf[Blob].boundingBox.radius)
    blobVelocity.sum / blobVelocity.length */
    blobGeneticCharacteristic(entities, _.asInstanceOf[Blob].boundingBox.radius)
  }

  val fovAverage: Set[SimulableEntity] => Double = (entities: Set[SimulableEntity]) => blobGeneticCharacteristic(entities, _.asInstanceOf[Blob].fieldOfViewRadius)

  val blobGeneticCharacteristic: (Set[SimulableEntity], SimulableEntity => Int) => Double = (entities, getGeneticChar) => {
    val blobVelocity = entities.filter(_.isInstanceOf[Blob]).toList.map(getGeneticChar)
    blobVelocity.sum / blobVelocity.length
  }

}
