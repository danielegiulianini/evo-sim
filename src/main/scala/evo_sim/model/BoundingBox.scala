package evo_sim.model



trait Shape
case class Rectangle(height: Int, width: Int) extends Shape
case class Circle(radius: Int) extends Shape
case class Triangle(side1: Int, side2: Int, side3: Int) extends Shape


