package evo_sim.view.swing.chart

import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle
import org.knowm.xchart.style.markers.SeriesMarkers
import org.knowm.xchart.{CategoryChart, CategoryChartBuilder, XYChart}

object ChartsFactory {

  def xyChart(width: Int, height: Int, series: Array[Double]): XYChart = {
    val chart = new XYChart(200, 200)
    chart.addSeries("population", series)
    chart
  }

  def histogramChart(width: Int, height: Int, populationSeries: Array[Double], foodSeries: Array[Double]): CategoryChart = {
    val chart = new CategoryChartBuilder().width(width).height(height).build()
    import org.knowm.xchart.style.Styler.LegendPosition
    chart.getStyler.setLegendPosition(LegendPosition.InsideNW)
    chart.getStyler.setAvailableSpaceFill(.96)
    chart.getStyler.setOverlapped(true)

    chart.addSeries("population", List.range(0, populationSeries.size).map(_.toDouble).toArray, populationSeries)
    val categorySeries = chart.addSeries("food", List.range(0,foodSeries.size).map(_.toDouble).toArray, foodSeries)
    categorySeries.setChartCategorySeriesRenderStyle(CategorySeriesRenderStyle.Line)
    categorySeries.setMarker(SeriesMarkers.NONE)
    //categorySeries.setLineStyle(new BasicStroke(BasicStroke.JOIN_ROUND))
    chart
  }


}
