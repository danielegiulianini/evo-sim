package evo_sim.view.swing.chart

import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle
import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.markers.Marker

object Series {

  trait SeriesStructure {
    def name : String
  }

  trait CartesianAxesSeriesStructure extends  SeriesStructure {
    def xData: List[Double]
    def yData: List[Double]
    def marker: Marker
  }

  case class CategorySeries(override val name: String,
                            override val xData: List[Double],
                            override val yData: List[Double],
                            override val marker: Marker,
                            seriesStyle: CategorySeriesRenderStyle) extends CartesianAxesSeriesStructure

  case class XySeries (override val name: String,
                       override val xData: List[Double],
                       override val yData: List[Double],
                       override val marker: Marker,
                       seriesStyle: XYSeries.XYSeriesRenderStyle) extends CartesianAxesSeriesStructure

  case class PieValue(name: String, value: Double)

}
