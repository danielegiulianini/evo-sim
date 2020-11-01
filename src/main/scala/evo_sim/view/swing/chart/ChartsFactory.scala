package evo_sim.view.swing.chart

import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle
import org.knowm.xchart._
import org.knowm.xchart.style.Styler.LegendPosition
import org.knowm.xchart.style.markers.Marker

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

object ChartsFactory {

  implicit def listToDoubleArray(list: List[Double]): Array[Double] = list.toArray

  def xyChart(width: Int, height: Int, multipleSeries: XySeries*): XYChart = {
    val chart = new XYChart(width, height)
    multipleSeries foreach(series => {
      val xySeries = chart.addSeries(series.name, series.xData, series.yData)
      xySeries.setMarker(series.marker)
      xySeries.setXYSeriesRenderStyle(series.seriesStyle)
    })
    chart
  }

  def histogramChart(width: Int, height: Int, multipleSeries: CategorySeries*): CategoryChart = {
    val chart = new CategoryChart(width, height)
    chart.getStyler.setLegendPosition(LegendPosition.InsideNE)
    chart.getStyler.setAvailableSpaceFill(.96)
    chart.getStyler.setOverlapped(true)

    multipleSeries foreach(series => {
      val categorySeries = chart.addSeries(series.name, series.xData, series.yData)
      categorySeries.setMarker(series.marker)
      categorySeries.setChartCategorySeriesRenderStyle(series.seriesStyle)
    })

    chart
  }

  def pieChart(width: Int, height: Int, values: PieValue*): PieChart = {
    val chart = new PieChart(width, height)
    values foreach( value => chart.addSeries(value.name, value.value))
    chart
  }

}
