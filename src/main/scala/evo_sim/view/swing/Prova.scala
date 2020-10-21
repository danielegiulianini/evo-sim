package evo_sim.view.swing

import cats.effect.{ExitCode, IO, IOApp}
import javax.swing.{JButton, JFrame, JPanel, WindowConstants}

import scala.swing.Dimension

object Prova extends IOApp {

  def panelCreated: IO[JPanel] = IO pure new JPanel

  def frameCreated: IO[JFrame] = IO pure new JFrame

  def buttonCreated(text: String): IO[JButton] = IO pure new JButton(text)

  def frameProps(frame: JFrame, panel: JPanel): IO[Unit] =
    for {
      _ <- IO apply frame.getContentPane.add(panel)
      _ <- IO.apply(frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE))
      _ <- IO.apply(frame.pack())
      _ <- IO.apply(frame.setResizable(false))
      _ <- IO.apply(frame.setVisible(true))
    } yield ()

  def initView: IO[Unit] =
    for {
      frame <- frameCreated
      panel <- panelCreated
      button <- buttonCreated("Ciao")
      _ <- IO apply panel.add(button)
      dimension <- IO pure new Dimension(1280, 720)
      _ <- IO apply panel.setPreferredSize(dimension)
      _ <- frameProps(frame, panel)
    } yield ()

  override def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- initView
    } yield ExitCode.Success
}
