package evo_sim.model

import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.EntityStructure.{Entity, Intelligent}
import evo_sim.prolog.MovingStrategiesProlog

import scala.math._

case class Movement(point: Point2D, direction: Direction)
case class Direction(angle: Int, stepToNextDirection: Int)

object MovingStrategies {

  /**  */
  def baseMovement(entity: Intelligent, world: World, entitiesFilter: Entity => Boolean): Movement = {
    val chasedEntity = (world.entities - entity.asInstanceOf[SimulableEntity]).filter(entitiesFilter) match {
      case set if set.nonEmpty => Option(set.minBy(elem => distanceBetweenEntities(entity.boundingBox.point, elem.boundingBox.point)))
      case _ => None
    }

    chasedEntity match {
      case Some(chasedEntity) if distanceBetweenEntities(entity.boundingBox.point, chasedEntity.boundingBox.point) < entity.fieldOfViewRadius => MovingStrategiesProlog.chaseMovement(entity, chasedEntity.boundingBox.point)
      case _ => MovingStrategiesProlog.standardMovement(entity)
    }
  }

  private def distanceBetweenEntities(a: Point2D, b: Point2D): Double = {
    sqrt(pow(b.x - a.x, 2) + pow(b.y - a.y, 2))
  }

}

/* history <- IO { world.reverse}

    /* dimensioni <- IO { frame.component.getSize }
    _ <- IO { println(dimensioni) } */

    xIterationData <- IO { List.range(0, (world.head.currentIteration+1)/Constants.ITERATIONS_PER_DAY+1).map(_.toDouble) }

    velocity <- IO { XySeries("velocity", xIterationData, averageDuringDay(history)(velocityAverage), SeriesMarkers.NONE, XYSeriesRenderStyle.Line)}
    dimension <- IO { XySeries("dimension", xIterationData,  averageDuringDay(history)(dimensionAverage), SeriesMarkers.NONE, XYSeriesRenderStyle.Line)}
    population <- IO { CategorySeries("population", xIterationData, averageDayEnd(history)(blobQuantity), SeriesMarkers.NONE, CategorySeriesRenderStyle.Bar)}
    food <- IO { CategorySeries("food", xIterationData, averageDayEnd(history)(foodQuantity), SeriesMarkers.NONE, CategorySeriesRenderStyle.Line)}


    _ <- IO { println(xIterationData) }
    _ <- IO { println(population.yData.size) }
    _ <- IO { println(velocity) }

    populationChart <- IO { ChartsFactory.histogramChart(675, 300, population, food)}
    populationChartPanel <- IO { new JComponentIO(new XChartPanel(populationChart)) }

    velocityChart <- IO { ChartsFactory.xyChart(675, 300, velocity, dimension) }
    velocityChartPanel <- IO { new JComponentIO(new XChartPanel(velocityChart)) }

    typologyChart <- IO { ChartsFactory.pieChart(400, 200)}
    typologyChartPanel <- IO { new JComponentIO(new XChartPanel(typologyChart)) }

    panel <- JPanelIO()
    _ <- panel.added(populationChartPanel)
    - <- panel.added(velocityChartPanel)
    _ <- panel.added(typologyChartPanel)

    cp <- frame.contentPane()
    _ <- cp.allRemovedInvokingAndWaiting()
    _ <- cp.addedInvokingAndWaiting(panel)
    _ <- frame.packedInvokingAndWaiting()
    _ <- frame.visibleInvokingAndWaiting(true)
  } yield () */

/*def velocityAverage(history: WorldHistory): List[Double] = {
    val origin = iterationVelocityAverage(history.head.entities)
    val speedAverageDays = history.grouped(Constants.ITERATIONS_PER_DAY).map(_.map(singleIteration => iterationVelocityAverage(singleIteration.entities)).sum / Constants.ITERATIONS_PER_DAY).toList
    origin :: speedAverageDays
  }

  def dimensionAverage(history: WorldHistory): List[Double] = {
    val origin = iterationDimensionAverage(history.head.entities)
    val dimensionAverageDays = history.grouped(Constants.ITERATIONS_PER_DAY).map(_.map(singleIteration => iterationDimensionAverage(singleIteration.entities)).sum / Constants.ITERATIONS_PER_DAY).toList
    origin :: dimensionAverageDays
  }

  def iterationVelocityAverage(entities: Set[SimulableEntity]): Double = {
    val blobVelocity = entities.filter(_.isInstanceOf[Blob]).toList.map(_.asInstanceOf[Blob].velocity)
    blobVelocity.sum / blobVelocity.length
  }

  def iterationDimensionAverage(entities: Set[SimulableEntity]): Double = {
    val blobVelocity = entities.filter(_.isInstanceOf[Blob]).toList.map(_.asInstanceOf[Blob].boundingBox.radius)
    blobVelocity.sum / blobVelocity.length
  }
  */
