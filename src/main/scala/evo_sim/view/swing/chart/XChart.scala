package evo_sim.view.swing.chart

import javax.swing.{JFrame, WindowConstants}
import org.knowm.xchart.{XChartPanel, XYChartBuilder}

object View extends JFrame {

  def initGUI():Unit = {
    val data1 = List.range(0, 10).map(elem => elem.toDouble).toArray
    val data2 = List.range(10, 20).map(elem => elem.toDouble).toArray
    val chart = new XYChartBuilder().build()
    chart.addSeries("prova1", data1, data2)
    chart.addSeries("prova2", data1, data1)

    val prova = new XChartPanel(chart)

    setContentPane(prova)
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
  new SwingWrapper(l, 2,2).displayChartMatrix()*/

  View.initGUI()
}
