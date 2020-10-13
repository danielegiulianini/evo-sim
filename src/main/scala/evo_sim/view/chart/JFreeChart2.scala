package evo_sim.view.chart

import evo_sim.view.chart.JFreeChart.{JFreeChart, XYSeries, XYSeriesCollection}
import javax.swing.JFrame
import org.jfree.chart.{ChartFactory, ChartPanel}

object JFreeChart2 extends JFrame{

  def initGUI(): Unit = {
    val born = List.iterate(0, 5)(_ + 3)
    val death = List.iterate(0, 5)(_ + 1)
    val prova = new XYSeries("Prova")
    val prova2 = new XYSeries("Prova2")
    for(elem <- 0 to 4){
      prova.add(elem, born(elem))
      prova2.add(elem, death(elem))
    }

    val dataset = new XYSeriesCollection
    dataset.addSeries(prova)
    dataset.addSeries(prova2)

    val chart: JFreeChart = ChartFactory.createXYLineChart("Example", "Day", "Number", dataset)

    /*val renderer = new XYLineAndShapeRenderer()
    renderer.setSeriesPaint(0, java.awt.Color.RED)
    renderer.setSeriesStroke(0, new java.awt.BasicStroke(2.0f))
    val plot = chart.getXYPlot
    plot.setRenderer(renderer)*/

    val panel = new ChartPanel(chart)
    add(panel)
    setSize(600, 400)
    setVisible(true)
    import javax.swing.WindowConstants
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  }
}

object Main extends App {
  JFreeChart2.initGUI()
}
