package Scala

import com.sun.javaws.exceptions.InvalidArgumentException

object Painter {

  class Point(var x: Int, var y: Int, var color: String)
  class Shape(var points: List[Point])

  sealed abstract class Command
  case class LineCommand(x1: Int, y1: Int, x2: Int, y2: Int) extends Command
  case class RectangleCommand(x1: Int, y1: Int, x2: Int, y2: Int) extends Command
  case class CircleCommand(x1: Int, y1: Int, r: Int) extends Command
  case class BoundingBoxCommand(x1: Int, y1: Int, x2: Int, y2: Int) extends Command
  case class UnknownCommand() extends Command

  /*sealed abstract class Shape(var points: List[Point])
  }
  case class Line(points: List[Point]) extends Shape
  case class BoundingBox(shapesInside: List[Shape]) extends Shape
  case class Rectangle(points: List[Point]) extends Shape
  case class Circle(points: List[Point]) extends Shape */


  def Draw(program: String): List[Shape] = {
    // split the program into commands
    val commands = program.split('\n')

    val interpolatedCommands = commands.map(c => InterpretCommand(c)).toList

    CommandsToShapes(interpolatedCommands);
  }

  // reference: https://stackoverflow.com/questions/10804581/read-case-class-object-from-string-in-scala-something-like-haskells-read-typ
  // check how to regex integers^
  def InterpretCommand(command: String): Command = command match {
    case s"(BOUNDING-BOX ($x1 $y1) ($x2 $y2))" => BoundingBoxCommand(x1.toInt,y1.toInt,x2.toInt,y2.toInt)
    case s"(LINE ($x1 $y1) ($x2 $y2))" => LineCommand(x1.toInt,y1.toInt,x2.toInt,y2.toInt)
    case s"(RECTANGLE ($x1 $y1) ($x2 $y2))" => RectangleCommand(x1.toInt,y1.toInt,x2.toInt,y2.toInt)
    case s"(CIRCLE ($x1 $y1) $r)" => CircleCommand(x1.toInt, y1.toInt, r.toInt)
    case _ => UnknownCommand() // TODO throw exception?
  }

  def CommandsToShapes(commands: List[Command]): List[Shape] = commands match {
    case BoundingBoxCommand(x1,y1,x2,y2) :: _ => RemoveCoordinatesOutsideBoundingArea(x1, y1, x2, y2, CommandsToShapes(commands.tail))
    case LineCommand(x1, y1, x2, y2) :: _ => new Shape(Line(x1,y1,x2,y2)) :: CommandsToShapes(commands.tail)
    case RectangleCommand(x1, y1, x2, y2) :: _ => RectTest(x1,y1,x2,y2) :: CommandsToShapes(commands.tail)
    case CircleCommand(x1, y1, r):: _ => CircleTest(x1, y1, r) :: CommandsToShapes(commands.tail)
    case _ => List.empty
  }

  def RemoveCoordinatesOutsideBoundingArea(x1: Int, y1: Int, x2: Int, y2: Int, shapesInside: List[Shape]): List[Shape] = {
    shapesInside.map(s => new Shape(s.points.filter(p => {
      p.x < x2 && p.x > x1 && p.y < y2 && p.y > y1
    })))
  }

  def CircleTest(x1: Int, y1: Int, r: Int): Shape = {
    new Shape(List(new Point(x1,y1,"red"), new Point(x1 + 5,y1 + 5, "red")))
  }

  def RectTest(x1: Int, y1: Int, x2: Int, y2: Int): Shape = {
    new Shape(List(new Point(x1,y2,"green"), new Point(x2,y2, "green")))
  }



  def Line(x1: Int, y1: Int, x2: Int, y2: Int, color: String = "black", slopeError: Int = 0): List[Point] = {
    if(x1 >= x2) {
      return List[Point](new Point(x2, y2, color))
    }

    if(slopeError + 2 * (y2 - y1) >= 0) {
      new Point(x1, y1, color) :: Line(x1 + 1, y1 + 1, x2, y2, color, slopeError - 2 * (x2 - x1) + 2 * (y2 - y1))
    } else {
      new Point(x1, y1, color) :: Line(x1 + 1, y1, x2, y2, color, slopeError + 2 * (y2 - y1))
    }
  }

  def CalcCircle(x0: Int, y0: Int, x: Int, y: Int, r: Int, p: Int): List[(Int, Int)] = {
    if (x < y) {
      return List.empty;
    }

    if(p <= 0) {
      // If the point is not on the line x = y - if it is the opposite points is already added
      if(x != y) {
        // One point for each octant + one point for each opposite
        (y + x0, x + y0) :: (-y + x0, x + y0) :: (y + x0, -x + y0) :: (-y + x0, -x + y0) :: (x + x0, y + y0) :: (-x + x0, y + y0) :: (x + x0, -y + y0) :: (-x + x0, -y + y0) :: CalcCircle(x0, y0, x, y + 1, r, p + 2 * (y + 1) + 1)
      } else {
        // One point for each octant
        (x + x0, y + y0) :: (-x + x0, y + y0) :: (x + x0, -y + y0) :: (-x + x0, -y + y0) :: CalcCircle(x0, y0, x, y + 1, r, p + 2 * (y + 1) + 1)
      }
    } else {
      if(x != y) {
        (y + x0, x + y0) :: (-y + x0, x + y0) :: (y + x0, -x + y0) :: (-y + x0, -x + y0) :: (x + x0, y + y0) :: (-x + x0, y + y0) :: (x + x0, -y + y0) :: (-x + x0, -y + y0) :: CalcCircle(x0, y0, x - 1, y + 1, r, p + 2 * (y + 1) - 2 * (x - 1) + 1)
      } else {
        (x + x0, y + y0) :: (-x + x0, y + y0) :: (x + x0, -y + y0) :: (-x + x0, -y + y0) :: CalcCircle(x0, y0, x - 1, y + 1, r, p + 2 * (y + 1) - 2 * (x - 1) + 1)
      }
    }
  }

  def Circle(x0: Int, y0: Int, r: Int): List[(Int, Int)] = {
    // Wrapping in a function to specify initial values
    CalcCircle(x0, y0, r, 0, r, 1 - r)
  }

  def CalcVerticalSide(x1: Int, y1: Int, x2: Int, y2: Int): List[(Int, Int)] = {
    if(y1 >= y2) {
      return List[(Int, Int)]((x2, y2))
    }

    (x1, y1) :: CalcVerticalSide(x1, y1 + 1, x2, y2);
  }

  def CalcHorizontalSide(x1: Int, y1: Int, x2: Int, y2: Int): List[(Int, Int)] = {
    if(x1 >= x2) {
      return List[(Int, Int)]((x2, y2))
    }

    (x1, y1) :: CalcHorizontalSide(x1 + 1, y1, x2, y2);
  }

  def Rect(x1: Int, y1: Int, x2: Int, y2: Int): List[(Int, Int)] = {
    CalcVerticalSide(x1, y1, x1, y2) ++ CalcHorizontalSide(x1, y2, x2, y2) ++ CalcVerticalSide(x2, y1, x2, y2) ++ CalcHorizontalSide(x1, y1, x2, y1)
  }
}
