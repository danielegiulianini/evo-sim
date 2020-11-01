package evo_sim.view.swing.chart

import evo_sim.view.swing.chart.Series.{CategorySeries, PieValue, XySeries}
import org.knowm.xchart._
import org.knowm.xchart.style.Styler.LegendPosition

object ChartsFactory {

  implicit def listToDoubleArray(list: List[Double]): Array[Double] = list.toArray

  def xyChart(title: String, width: Int, height: Int, multipleSeries: XySeries*): XYChart = {
    val chart = new XYChartBuilder().title(title).width(width).height(height).build()
    multipleSeries foreach(series => {
      val xySeries = chart.addSeries(series.name, series.xData, series.yData)
      xySeries.setMarker(series.marker)
      xySeries.setXYSeriesRenderStyle(series.seriesStyle)
    })
    chart
  }

  def histogramChart(title: String, width: Int, height: Int, multipleSeries: CategorySeries*): CategoryChart = {
    val chart = new CategoryChartBuilder().title(title).width(width).height(height).build()
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

  def pieChart(title: String, width: Int, height: Int, indicators: PieValue*): PieChart = {
    val chart = new PieChartBuilder().title(title).width(width).height(height).build()
    indicators foreach( indicator => chart.addSeries(indicator.name, indicator.value))
    chart
  }

}
