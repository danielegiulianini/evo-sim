package evo_sim.controller

import scala.concurrent.ExecutionContext

object HandlerExecutionContext {
  def immediateContext: ExecutionContext = new ExecutionContext {
    def execute(runnable: Runnable) {
      runnable.run()
    }

    def reportFailure(cause: Throwable): Unit = {
      cause.printStackTrace()
      System.exit(-1)
    }
  }
}
