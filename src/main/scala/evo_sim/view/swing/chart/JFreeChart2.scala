package evo_sim.view.swing.chart

import javax.swing.{JFrame, JPanel}

object JFreeChart2 extends JFrame with scalax.chart.module.Charting {

  /*def initGUI(): Unit = {
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
  }*/

  def initGUI2(): Unit = {
    val data = for (i <- 1 to 5) yield (i,i)
    val data2 = for (i <- 1 to 5) yield (i/2,i*2)
    val dataset = data.toXYSeriesCollection("nascite")
    dataset.addSeries(data2.toXYSeries())
    val chart = XYLineChart(dataset)
    val chartJFree2 = chart.toComponent.peer
    val chartJFree = chart.peer
    //val panel = new ChartPanel(chartJFree)
    val panel = new JPanel()
    panel.add(chartJFree2)
    add(panel)
    setSize(600, 400)
    setVisible(true)
    import javax.swing.WindowConstants
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  }
}

object Main extends App {
  JFreeChart2.initGUI2()
}
