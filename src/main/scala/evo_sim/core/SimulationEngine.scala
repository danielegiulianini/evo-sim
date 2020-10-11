package evo_sim.core


import cats.data.StateT
import cats.effect.{ContextShift, IO}
import cats.effect.IO.fromFuture
import evo_sim.model.EntityBehaviour.SimulableEntity
import evo_sim.model.World
import evo_sim.model.World._

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

object SimulationEngine {


  type SimulationIO[A] = IO[A]    //could be not generic: type SimulationIO = IO[Unit]
  type Simulation[A] = StateT[SimulationIO, World, A] //type Simulation = StateT[SimulationIO, World, Unit]

  def liftIo[A](v: SimulationIO[A]): Simulation[A] = StateT[SimulationIO, World, A](s => v.map((s, _)))

  def toStateT[A](f: World => (World, A)): Simulation[A] = StateT[IO, World, A](s => IO(f(s)))

  //function to create StateMonad from a World to World function
  def toStateTWorld(f: World => World): Simulation[World] = toStateT[World]( w => toTuple(f(w)))
  def toTuple[A](a:A) = (a, a)


  /*val worldUpdatedVal = (world: World) =>
    World(
      world.width,
      world.height,
      world.currentIteration + 1,
      world.entities.foldLeft(world)((updatedWorld, entity) =>
        World (
          world.width,
          world.height,
          world.currentIteration,
          entity.updated(updatedWorld)
        )
      ).entities
    )*/

  def worldUpdated2(): Simulation[World] = toStateTWorld { worldUpdated _  }

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  def started() =
    for {
      _ <- IO { println("initializing") }
      - <- IO { ViewModule.GUIBuilt() }
      env <- fromFuture(IO(ViewModule.inputReadFromUser()))
      _ <- IO { simulationLoop().runS(worldCreated(env))}
    } yield()

  def worldUpdated(world: World): World = {
    World(
      world.width,
      world.height,
      world.currentIteration + 1,
      world.entities.foldLeft(world)((updatedWorld, entity) =>
        World (
          world.width,
          world.height,
          world.currentIteration,
          entity.updated(updatedWorld)
        )
      ).entities
    )
  }

  def collisionsHandled(world: World): World = {
    def collisions = for {
      i <- world.entities
      j <- world.entities
      if i != j // && i.intersected(j.shape)//intersects(j.shape)
    } yield (i, j)

    def entitiesAfterCollision =
      collisions.foldLeft(Set.empty[SimulableEntity])((entitiesAfterCollision, collision) => entitiesAfterCollision ++ collision._1.collided(collision._2))

    World(
      world.width,
      world.height,
      world.currentIteration,
      entitiesAfterCollision//world.entities
    )
  }

  /*//to be refactored in functional way
  def started(): Unit = {
    ViewModule.GUIBuilt()
    ViewModule.inputReadFromUser().onComplete(e => {
      val environment = e.get
      val world = worldCreated(environment)
      ViewModule.simulationGUIBuilt()
      simulationLoop(world)
    })*/

  //to be refactored in functional way
  /*def simulationLoop(world: World): Unit = {
    val updatedWorld = worldUpdated(world)
    val worldAfterCollisions = collisionsHandled(updatedWorld)
    ViewModule.rendered(worldAfterCollisions)
  }*/

  def simulationLoop() = for { _  <- liftIo(IO {})} yield ()
}
