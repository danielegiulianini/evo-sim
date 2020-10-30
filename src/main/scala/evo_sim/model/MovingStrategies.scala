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

/*
chart <- IO { new XYChart(200,200)}
    _ <- IO { chart.addSeries("prova", List.range(0,10).map(_.toDouble).toArray) }

    chartPanel <- IO { new JComponentIO(new XChartPanel[XYChart](chart)) }
    chartPanel2 <- IO { new JComponentIO(new XChartPanel[XYChart](chart)) }
    _ <- IO { println(chartPanel.component.getWidth) }

    /* panel <- JPanelIO()
    _ <- IO { panel.layoutSet(new VerticalLayout()) }
    _ <- IO { panel.added(chartPanel) }
    _ <- IO { panel.added(chartPanel2) } */

    cp <- frame.contentPane()
    _ <- cp.allRemovedInvokingAndWaiting()
    _ <- cp.addedInvokingAndWaiting(chartPanel2, BorderLayout.NORTH)
    _ <- cp.addedInvokingAndWaiting(chartPanel, BorderLayout.CENTER)
    _ <- frame.packedInvokingAndWaiting()
    _ <- frame.visibleInvokingAndWaiting(true)
 */
