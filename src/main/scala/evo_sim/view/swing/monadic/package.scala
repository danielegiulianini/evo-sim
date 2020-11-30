package evo_sim.view.swing

import java.awt.event.{ActionEvent, ActionListener}

import cats.effect.IO
import javax.swing.SwingUtilities
import javax.swing.event.ChangeEvent

/** A monadic wrapper for Swing and Awt main components that provides an access point to the Swing APIs with
 * a monadic taste. It is independent from the simulator domain and can be potentially re-used outside this
 * project.
 * Instead of directly executing swing's components' methods and utilities, the classes of this package
 * returns the corresponding [[IO]] monad descriptions, actually decoupling description from execution.
 */
package object monadic {
  /**
   * Returns the [[IO]] containing the code for synchronously executing the computation described inside the
   * computation parameter on the AWT event dispatching thread, in a thread-safe manner.
   * This method should be used whenever a thread other than EDT needs to update the GUI.
   * Unlike [[invokingLater]], upon calling "unsafeRunSync" on the IO returned, the control flow
   * will block until all pending AWT events have been processed and only then the
   * [[SwingUtilities#invokeLater]] wrapped call will return.
   * If searching for an asynchronous execution of [[IO]]'s content, must use [[invokingLater]] instead.
   *
   * @see [[evo_sim.view.swing.monadic.ExampleWithLayoutWithSwingMonadic]] for examples of how to use it
   *          inside a monadic chain.
   * @param computation the [[IO]] containing the GUI-related logic to be executed.
   * @return an IO wrapping the [[SwingUtilities#invokeAndWait]] thread-safe call.
   */
  def invokingAndWaiting(computation: IO[_]): IO[Unit] = IO {
    SwingUtilities.invokeAndWait(() => computation.unsafeRunSync())
  }

  /**
   * Returns the [[IO]] containing the code for asynchronously executing the computation described inside the
   * given IO parameter on the AWT event dispatching thread, in a thread-safe manner.
   * This method should be used whenever a thread other than EDT needs to asynchronously update the GUI.
   * Unlike [[invokingAndWaiting()]], upon calling "unsafeRunSync" on the IO returned, the control flow
   * will immediately return from the [[SwingUtilities#invokeLater]] call wrapped in the IO returned and
   * the GUI-related code will be executed after all pending AWT events have been processed.
   * The actual execution of the [[IO]] return should not happen on the EDT thread without a proper
   * thread or context-shifting mechanism.
   *
   * @param computation the [[IO]] containing the GUI-related logic to be executed.
   * @return an IO wrapping the [[SwingUtilities#invokeLater]] thread-safe call.
   */
  def invokingLater(computation: IO[_]): IO[Unit] = IO {
    SwingUtilities.invokeLater(() => computation.unsafeRunSync())
  }

  /** Type alias for a monadic listener, i.e. a listener whose behaviour upon [[ActionEvent]] is described by an IO
   * monad (possibly result of a composition of IO monads).
   * It is the alternative to [[ActionListener]] and it is used in
   * [[evo_sim.view.swing.monadic.JButtonIO.monadicActionListenerAdded]].
   * @see [[evo_sim.view.swing.monadic.ExampleWithMonadicVsProceduralListeners]] for examples.*/
  type MonadicActionListener = ActionEvent => IO[Unit]

  /** Implicit utility for converting a by-name (or unevaluated) parameter expression provided by => syntax to
   * [[ActionListener]] for enabling a more concise syntax at call-side when describing listeners that ignores
   * the [[ActionEvent]] triggering.*/
  implicit def unitToActionListener(f: =>Unit): ActionListener = _ => f

  /** Implicit utility for converting a by-name (or unevaluated) parameter expression provided by => syntax to
   * [[MonadicActionListener]] for enabling a more concise syntax at call-side when describing listeners that ignores
   * the [[ActionEvent]] triggering.*/
  implicit def unitToMonadicActionListener(f: => IO[Unit]): MonadicActionListener = _ => f

  /** Type alias for a monadic change listener, i.e. a listener whose behaviour upon [[ChangeEvent]] is
   * described by an IO monad (possibly result of a composition of IO monads).
   * It is the alternative to [[javax.swing.event.ChangeListener]] and it is used in
   * [[evo_sim.view.swing.monadic.JSliderIO.monadicChangeListenerAdded]]. */
  type MonadicChangeListener = ChangeEvent => IO[Unit]

  /** Implicit utility for converting a by-name (or unevaluated) parameter expression provided by => syntax to
   * [[MonadicChangeListener]] for enabling a more concise syntax at call-side when describing listeners that ignores
   * the [[ChangeEvent]] triggering.*/
  implicit def unitToMonadicChangeListener(f: => IO[Unit]): MonadicChangeListener = _ => f

}
