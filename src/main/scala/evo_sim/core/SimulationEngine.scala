package evo_sim.core


import cats.data.StateT
import cats.effect.{ContextShift, IO}
import cats.effect.IO.{fromFuture, unit}
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.Intersection.intersected
import evo_sim.model.World
import evo_sim.model.World._
import evo_sim.view.ViewModule

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

object SimulationEngine {

  type SimulationIO[A] = IO[A] //could be not generic: type SimulationIO = IO[Unit]
  type Simulation[A] = StateT[SimulationIO, World, A] //type Simulation = StateT[SimulationIO, World, Unit]

  //helper to create StateT monad from a IO monad
  def liftIo[A](v: SimulationIO[A]): Simulation[A] = StateT[SimulationIO, World, A](s => v.map((s, _)))

  def toStateT[A](f: World => (World, A)): Simulation[A] = StateT[IO, World, A](s => IO(f(s)))

  //function to create StateT monad from a World to World function
  def toStateTWorld(f: World => World): Simulation[World] = toStateT[World](w => toTuple(f(w)))

  def toTuple[A](a: A) = (a, a)

  def worldUpdated(): Simulation[World] = toStateTWorld {
    SimulationLogic.worldUpdated
  }

  def collisionsHandled(): Simulation[World] = toStateTWorld {
    SimulationLogic.collisionsHandled
  }

  implicit val timer = IO.timer(ExecutionContext.global)

  def getTime() = liftIo(IO(System.currentTimeMillis().millis))   //def getTime() = liftIo(IO( (w: World) => (w, System.currentTimeMillis().millis)) ) //as a statet monad returns a identical new world but x seconds older

  def waitUntil(from: FiniteDuration, period: FiniteDuration) =
    liftIo(IO(if (from < period) {
      IO.sleep((period - from))
    } else unit))

  def worldRendered(worldAfterCollisions: World) =
    liftIo(IO { ViewModule.rendered(worldAfterCollisions) })

  def inputReadFromUser() =
    IO { ViewModule.inputReadFromUser() }


  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  def started() = {
    println("from started currentThread is" + Thread.currentThread)
    for {
      _ <- IO {
        println("initializing")
      }
      - <- IO {
        println("building gui")
        ViewModule.GUIBuilt()
      }
      env <- inputReadFromUser()        //env <- fromFuture(IO(ViewModule.inputReadFromUser())) //if using promises
      _ <- IO {
        println("before sim loop call")
      }
      _ <- IO {
        println("calling sim loop")
        simulationLoop().runS(worldCreated(env)).unsafeRunSync()
      }
    } yield ()
  }


  def simulationLoop() = for {
    _ <- liftIo(IO({
      println("inside sim loop")
    }))
    startTime <- getTime
    _ <- worldUpdated()
    worldAfterCollisions <- collisionsHandled
    _ <- worldRendered(worldAfterCollisions)
    currentTime <- getTime
    _ <- waitUntil(currentTime - startTime, 1000 millis)
  } yield ()


  //maybe to move outside this object
  object SimulationLogic {
    def worldUpdated(world: World): World =
      World(
        world.width,
        world.height,
        world.currentIteration + 1,
        world.entities.foldLeft(Set[SimulableEntity]())((updatedEntities, entity) => updatedEntities ++ entity.updated(world)),
        world.totalIterations
      )

    def collisionsHandled(world: World): World = {
      def collisions = for {
        i <- world.entities
        j <- world.entities
        if i != j && intersected(i.boundingBox, j.boundingBox)
      } yield (i, j)

      def entitiesAfterCollision =
        collisions.foldLeft(Set.empty[SimulableEntity])((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

      World(
        world.width,
        world.height,
        world.currentIteration,
        entitiesAfterCollision ++ world.entities,
        world.totalIterations
      )
    }
  }

}
      /*
object SimulationEngine {

  def worldUpdated(world: World): World = {
    World(
      world.width,
      world.height,
      world.currentIteration + 1,
      world.entities.foldLeft(Set[SimulableEntity]())((updatedEntities, entity) => updatedEntities ++ entity.updated(world)),
      world.totalIterations
    )
  }


  def collisionsHandled(world: World): World = {
    def collisions = for {
      i <- world.entities
      j <- world.entities
      if i != j && intersected(i.boundingBox, j.boundingBox)
    } yield (i, j)

    def entitiesAfterCollision =
      collisions.foldLeft(Set.empty[SimulableEntity])((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    World(
      world.width,
      world.height,
      world.currentIteration,
      world.entities ++ entitiesAfterCollision,
      world.totalIterations
    )
  }

  def started(): Unit = {
    ViewModule.GUIBuilt()
    val environment = ViewModule.inputReadFromUser()
    val world = worldCreated(environment)
    ViewModule.simulationGUIBuilt()
    val startingTime = System.currentTimeMillis()
    ViewModule.rendered(world)
    val endingTime = System.currentTimeMillis() //val endingTime = System.nanoTime();
    val elapsed = endingTime - startingTime
    waitUntil(elapsed, 1000) //period in milliseconds
    simulationLoop(world)

    @scala.annotation.tailrec
    def simulationLoop(world: World): Unit = {
      println("iteration: " + world.currentIteration + "/ " + world.totalIterations)
      val startingTime = System.currentTimeMillis()
      val updatedWorld = worldUpdated(world)
      val worldAfterCollisions = collisionsHandled(updatedWorld)
      ViewModule.rendered(worldAfterCollisions)

      val endingTime = System.currentTimeMillis() //val endingTime = System.nanoTime();
      val elapsed = endingTime - startingTime

      waitUntil(elapsed, 1000) //period in milliseconds

      if (worldAfterCollisions.currentIteration < worldAfterCollisions.totalIterations)
        simulationLoop(worldAfterCollisions)
    }

    def waitUntil(from: Long, period: Long): Unit = {
      if (from < period)
        try
          Thread.sleep(period - from)
        catch {
          case _: InterruptedException =>
        }
    }
  }
}
*/