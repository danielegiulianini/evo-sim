package evo_sim.view.swing.chart

import javax.swing.{JFrame, JPanel, WindowConstants}
import org.knowm.xchart.style.markers.SeriesMarkers
import org.knowm.xchart.{XChartPanel, XYChartBuilder}

object View extends JFrame {

  def initGUI():Unit = {
    val data1 = List.range(0, 10).map(elem => elem.toDouble).toArray
    val data2 = List.range(10, 20).map(elem => elem.toDouble).toArray
    val chart = new XYChartBuilder().width(400).height(400).build()
    //val chart = new XYChart(400, 400)
    val series = chart.addSeries("prova1", data1, data2)
    chart.addSeries("prova2", data1, data1)
    series.setMarker(SeriesMarkers.NONE)

    //val panel = new JPanel()
    //panel.add(chart)

    val xChartPanel = new XChartPanel(chart)
    val xChartPanel2 = new XChartPanel(chart)

    val allChartPanel = new JPanel()

    allChartPanel.add(xChartPanel)
    allChartPanel.add(xChartPanel2)

    setContentPane(allChartPanel)
    setSize(900,600)
    setVisible(true)
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  }

}

object XChart extends App {
  /*val data1 = Array(0.0, 1.0, 2.0)
  val data2 = Array(0.0, 1.0, 2.0)
  val chart = QuickChart.getChart("prova", "X", "Y", "y(x)", data1, data2)
  val chart2 = QuickChart.getChart("prova", "X", "Y", "y(x)", data1, data2)
  val l = new java.util.ArrayList[org.knowm.xchart.XYChart]()
  l.add(chart)
  l.add(chart2)
  new SwingWrapper(l, 2, 1).displayChartMatrix()*/

  /*val data1 = List.range(0, 10).map(elem => elem.toDouble).toArray
  val data2 = List.range(10, 20).map(elem => elem.toDouble).toArray
  val chart = new XYChartBuilder().build()
  chart.addSeries("prova1", data1, data2)
  chart.addSeries("prova2", data1, data1)
  val l = new java.util.ArrayList[org.knowm.xchart.XYChart]()
  l.add(chart)
  l.add(chart)
  l.add(chart)
  l.add(chart)
  val prova = new SwingWrapper(l, 2,2).displayChartMatrix()*/

  View.initGUI()
}
