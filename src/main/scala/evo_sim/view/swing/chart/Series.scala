package evo_sim.view.swing.chart

import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle
import org.knowm.xchart.XYSeries
import org.knowm.xchart.style.markers.Marker

object Series {

  /**
   * This trait represent a standard series for the charts.
   */
  trait SeriesStructure {
    def name : String
  }

  /**
   * This trait represents a series for charts that need 2-Dimensional points.
   */
  trait CartesianAxesSeriesStructure extends SeriesStructure {
    def xData: List[Double]
    def yData: List[Double]
    def marker: Marker
  }

  /** Represent a series with [[CartesianAxesSeriesStructure]] for the [[org.knowm.xchart.CategoryChart]].
   *  In this series it is possible select the marker of the 2-Dimensional points and the visualization points style.
   *
   * @param name the name of the series.
   * @param xData the [[List]] of x coordinates.
   * @param yData the [[List]] of y coordinates.
   * @param marker the marker typology.
   * @param seriesStyle the visualization points style.
   */
  case class CategorySeries(override val name: String,
                            override val xData: List[Double],
                            override val yData: List[Double],
                            override val marker: Marker,
                            seriesStyle: CategorySeriesRenderStyle) extends CartesianAxesSeriesStructure

  /** Represent a series with [[CartesianAxesSeriesStructure]] for the [[org.knowm.xchart.XYChart]].
   *  In this series it is possible select the marker of the 2-Dimensional points and the visualization points style.
   *
   * @param name the name of the series.
   * @param xData the [[List]] of x coordinates.
   * @param yData the [[List]] of y coordinates.
   * @param marker the marker typology.
   * @param seriesStyle the visualization points style.
   */
  case class XySeries (override val name: String,
                       override val xData: List[Double],
                       override val yData: List[Double],
                       override val marker: Marker,
                       seriesStyle: XYSeries.XYSeriesRenderStyle) extends CartesianAxesSeriesStructure

  /** Represent a value for the [[org.knowm.xchart.PieChart]].
   *
   * @param name the name of the value.
   * @param value the value
   */
  case class PieValue(override val name: String,
                      value: Double) extends SeriesStructure

}
