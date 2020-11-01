package evo_sim.view.swing.chart

import scala.language.implicitConversions
import evo_sim.view.swing.chart.Series.{CategorySeries, PieValue, XySeries}
import org.knowm.xchart._
import org.knowm.xchart.style.Styler.LegendPosition

object ChartsFactory {

  /** Implicit conversion from [[List]] to [[Array]]
   *
   * @param list the list to convert.
   * @return the [[Array]] equivalent at the [[List]]
   */
  implicit def listToDoubleArray(list: List[Double]): Array[Double] = list.toArray

  /** Creates an [[XYChart]] with a specific title, width and height. The chart displays
   * the [[XySeries]] passed in the parameters.
   *
   * @param title the title of the chart.
   * @param width the width of the chart.
   * @param height the height of the chart.
   * @param multipleSeries the series displayed in the chart.
   * @return an instance of [[XYChart]] with a specific title, width and height.
   */
  def xyChart(title: String, width: Int, height: Int, multipleSeries: XySeries*): XYChart = {
    val chart = new XYChartBuilder().title(title).width(width).height(height).build()
    multipleSeries foreach(series => {
      val xySeries = chart.addSeries(series.name, series.xData, series.yData)
      xySeries.setMarker(series.marker)
      xySeries.setXYSeriesRenderStyle(series.seriesStyle)
    })
    chart
  }

  /** Creates an [[CategoryChart]] with a specific title, width and height. The chart displays
   * the [[CategorySeries]] passed in the parameters.
   *
   * @param title the title of the chart.
   * @param width the width of the chart.
   * @param height the height of the chart.
   * @param multipleSeries the series displayed in the chart.
   * @return an instance of [[CategoryChart]] with a specific title, width and height.
   */
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

  /** Creates an [[PieChart]] with a specific title, width and height. The chart displays
   * the [[PieValue]] passed in the parameters.
   *
   * @param title the title of the chart.
   * @param width the width of the chart.
   * @param height the height of the chart.
   * @param indicators the percentage displayed in the chart.
   * @return an instance of [[PieChart]] with a specific title, width and height.
   */
  def pieChart(title: String, width: Int, height: Int, indicators: PieValue*): PieChart = {
    val chart = new PieChartBuilder().title(title).width(width).height(height).build()
    indicators foreach( indicator => chart.addSeries(indicator.name, indicator.value))
    chart
  }

}
