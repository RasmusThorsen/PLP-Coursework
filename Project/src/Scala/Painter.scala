package Scala

import com.sun.javaws.exceptions.InvalidArgumentException

object Painter {

  class Point(var x: Int, var y: Int, var color: String)
  class Shape(var points: List[Point])

  sealed abstract class Command
  case class LineCommand(x1: Int, y1: Int, x2: Int, y2: Int, color: String ) extends Command
  case class RectangleCommand(x1: Int, y1: Int, x2: Int, y2: Int, color: String) extends Command
  case class CircleCommand(x1: Int, y1: Int, r: Int, color: String) extends Command
  case class BoundingBoxCommand(x1: Int, y1: Int, x2: Int, y2: Int) extends Command
  case class TextCommand(x1: Int, y1: Int, text: String, color: String) extends Command
  case class DrawCommand(color: String, rawObjects: String, lineNumber: Int) extends Command
  case class UnknownCommand() extends Command

  def Draw(program: String): List[Shape] = {
    // split the program into commands
    val commands = program.split('\n')

    val interpolatedCommands = commands.zipWithIndex.map(c => InterpolateCommand(c._1, c._2+1)).toList

    CommandsToShapes(interpolatedCommands);
  }

  // reference: https://stackoverflow.com/questions/10804581/read-case-class-object-from-string-in-scala-something-like-haskells-read-typ
  // check how to regex integers^
  def InterpolateCommand(command: String, lineNumber: Int, color: String = "black"): Command = command match {
    case s"(BOUNDING-BOX ($x1 $y1) ($x2 $y2))" => BoundingBoxCommand(x1.toInt,y1.toInt,x2.toInt,y2.toInt)
    case s"(LINE ($x1 $y1) ($x2 $y2))" => LineCommand(x1.toInt,y1.toInt,x2.toInt,y2.toInt,color)
    case s"(RECTANGLE ($x1 $y1) ($x2 $y2))" => RectangleCommand(x1.toInt,y1.toInt,x2.toInt,y2.toInt,color)
    case s"(CIRCLE ($x1 $y1) $r)" => CircleCommand(x1.toInt, y1.toInt, r.toInt,color)
    case s"(TEXT-AT ($x1 $y1) $t" => TextCommand(x1.toInt, y1.toInt, t, color)
    case s"(DRAW $color $objects)" => DrawCommand(color, objects, lineNumber)
    case _ => throw new IllegalArgumentException(s"Error: Couldn't parse command at line ${lineNumber}. '${command}'")
  }

  def CommandsToShapes(commands: List[Command]): List[Shape] = commands match {
    case BoundingBoxCommand(x1,y1,x2,y2) :: _ => RemoveCoordinatesOutsideBoundingArea(x1, y1, x2, y2, CommandsToShapes(commands.tail))
    case LineCommand(x1, y1, x2, y2,color) :: _ => new Shape(Line(x1,y1,x2,y2,color)) :: CommandsToShapes(commands.tail)
    case RectangleCommand(x1, y1, x2, y2,color) :: _ => new Shape(Rect(x1,y1,x2,y2, color)) :: CommandsToShapes(commands.tail)
    case CircleCommand(x1, y1, r,color) :: _ => new Shape(Circle(x1, y1, r, color)) :: CommandsToShapes(commands.tail)
    case DrawCommand(color, objects, lineNumber) :: _ => InterpolateDrawCommand(color, objects, lineNumber) ::: CommandsToShapes(commands.tail)
    case _ => List.empty
  }

  def RemoveCoordinatesOutsideBoundingArea(x1: Int, y1: Int, x2: Int, y2: Int, shapesInside: List[Shape]): List[Shape] = {
    shapesInside.map(s => new Shape(s.points.filter(p => {
      p.x < x2 && p.x > x1 && p.y < y2 && p.y > y1
    })))
  }

  def InterpolateDrawCommand(color: String, objects: String, lineNumber: Int): List[Shape] = objects match {
    case s"($cmd)) $rest" => CommandsToShapes(List(InterpolateCommand(s"($cmd))", lineNumber, color))) ::: InterpolateDrawCommand(color, rest, lineNumber)
    case s"($cmd))" => CommandsToShapes(List(InterpolateCommand(s"($cmd))", lineNumber, color)))
    case s"($cmd $i1 $i2) $rest" => CommandsToShapes(List(InterpolateCommand(s"($cmd $i1 $i2)", lineNumber, color))) ::: InterpolateDrawCommand(color, rest, lineNumber)
    case s"($cmd $i1 $i2)" => CommandsToShapes(List(InterpolateCommand(s"($cmd $i1 $i2)", lineNumber, color)))
    case "" => List.empty
    case other => throw new IllegalArgumentException(s"Error: Couldn't parse command at line ${lineNumber}. '${other}'")
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

  def CalcCircle(x0: Int, y0: Int, x: Int, y: Int, r: Int, p: Int, color: String): List[Point] = {
    if (x < y) {
      return List.empty;
    }

    if(p <= 0) {
      // If the point is not on the line x = y - if it is the opposite points is already added
      if(x != y) {
        // One point for each octant + one point for each opposite
        new Point(y + x0, x + y0, color) :: new Point(-y + x0, x + y0, color) :: new Point(y + x0, -x + y0, color) :: new Point(-y + x0, -x + y0, color) :: new Point(x + x0, y + y0, color) :: new Point(-x + x0, y + y0, color) :: new Point(x + x0, -y + y0, color) :: new Point(-x + x0, -y + y0, color) :: CalcCircle(x0, y0, x, y + 1, r, p + 2 * (y + 1) + 1, color)
      } else {
        // One point for each octant
        new Point(x + x0, y + y0, color) :: new Point(-x + x0, y + y0, color) :: new Point(x + x0, -y + y0, color) :: new Point(-x + x0, -y + y0, color) :: CalcCircle(x0, y0, x, y + 1, r, p + 2 * (y + 1) + 1, color)
      }
    } else {
      if(x != y) {
        new Point(y + x0, x + y0, color) :: new Point(-y + x0, x + y0, color) :: new Point(y + x0, -x + y0, color) :: new Point(-y + x0, -x + y0, color) :: new Point(x + x0, y + y0, color) :: new Point(-x + x0, y + y0, color) :: new Point(x + x0, -y + y0, color) :: new Point(-x + x0, -y + y0, color) :: CalcCircle(x0, y0, x - 1, y + 1, r, p + 2 * (y + 1) - 2 * (x - 1) + 1, color)
      } else {
        new Point(x + x0, y + y0, color) :: new Point(-x + x0, y + y0, color) :: new Point(x + x0, -y + y0, color) :: new Point(-x + x0, -y + y0, color) :: CalcCircle(x0, y0, x - 1, y + 1, r, p + 2 * (y + 1) - 2 * (x - 1) + 1, color)
      }
    }
  }

  def Circle(x0: Int, y0: Int, r: Int, color: String): List[Point] = {
    // Wrapping in a function to specify initial values
    CalcCircle(x0, y0, r, 0, r, 1 - r, color)
  }

  def CalcVerticalSide(x1: Int, y1: Int, x2: Int, y2: Int, color: String): List[Point] = {
    if(y1 >= y2) {
      return List[Point](new Point(x2, y2, color))
    }

    new Point(x1, y1, color) :: CalcVerticalSide(x1, y1 + 1, x2, y2, color);
  }

  def CalcHorizontalSide(x1: Int, y1: Int, x2: Int, y2: Int, color: String): List[Point] = {
    if(x1 >= x2) {
      return List[Point](new Point(x2, y2, color))
    }

    new Point(x1, y1, color) :: CalcHorizontalSide(x1 + 1, y1, x2, y2, color);
  }

  def Rect(x1: Int, y1: Int, x2: Int, y2: Int, color: String): List[Point] = {
    CalcVerticalSide(x1, y1, x1, y2, color) ++ CalcHorizontalSide(x1, y2, x2, y2, color) ++ CalcVerticalSide(x2, y1, x2, y2, color) ++ CalcHorizontalSide(x1, y1, x2, y1, color)
  }
}
