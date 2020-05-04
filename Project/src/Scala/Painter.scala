package Scala

import scala.collection.immutable.HashSet
import scala.jdk.CollectionConverters._

object Painter {

  val gridGap = 30;

  class Point(var x: Int, var y: Int, var color: String)

  sealed abstract class Element()
  sealed class Shape(var points: List[Point]) extends Element
  case class Circle(ps: List[Point]) extends Shape(ps)
  case class Rectangle(ps: List[Point]) extends Shape(ps)
  case class Line(ps: List[Point]) extends Shape(ps)
  case class Text(var x: Double, var y: Double, var text: String, var color: String) extends Element

  sealed abstract class Command
  case class LineCommand(x1: Double, y1: Double, x2: Double, y2: Double, color: String ) extends Command
  case class RectangleCommand(x1: Double, y1: Double, x2: Double, y2: Double, color: String) extends Command
  case class CircleCommand(x1: Double, y1: Double, r: Double, color: String) extends Command
  case class BoundingBoxCommand(x1: Double, y1: Double, x2: Double, y2: Double) extends Command
  case class TextCommand(x: Double, y: Double, text: String, color: String) extends Command
  case class DrawCommand(color: String, rawElements: String, lineNumber: Int) extends Command
  case class FillCommand(strokeColor: String, fillColor: String, elementCommand: Command) extends Command
  case class EmptyLine() extends Command
  case class Comment() extends Command

  def Draw(program: String) = {
    // split the program into commands
    val commands = program.split('\n')

    val interpolatedCommands = commands.zipWithIndex.map(c => InterpolateCommand(c._1, c._2+1)).toList

    CommandsToElements(interpolatedCommands).asJava;
  }

  // reference: https://stackoverflow.com/questions/10804581/read-case-class-object-from-string-in-scala-something-like-haskells-read-typ
  // check how to regex integers^
  def InterpolateCommand(command: String, lineNumber: Int, color: String = "black"): Command = command match {
    case s"(BOUNDING-BOX ($x1 $y1) ($x2 $y2))" => BoundingBoxCommand(x1.toDouble * gridGap,y1.toDouble * gridGap,x2.toDouble * gridGap,y2.toDouble * gridGap)
    case s"(LINE ($x1 $y1) ($x2 $y2))" => LineCommand(x1.toDouble * gridGap,y1.toDouble * gridGap,x2.toDouble * gridGap,y2.toDouble * gridGap,color)
    case s"(RECTANGLE ($x1 $y1) ($x2 $y2))" => RectangleCommand(x1.toDouble * gridGap,y1.toDouble * gridGap,x2.toDouble * gridGap,y2.toDouble * gridGap,color)
    case s"(CIRCLE ($x1 $y1) $r)" => CircleCommand(x1.toDouble * gridGap, y1.toDouble * gridGap, r.toDouble * gridGap,color)
    case s"(TEXT-AT ($x1 $y1) $t)" => TextCommand(x1.toDouble * gridGap, y1.toDouble * gridGap, t, color)
    case s"(DRAW $color $elements)" => DrawCommand(color, elements, lineNumber)
    case s"(FILL $fillColor $element)" => FillCommand(color, fillColor, InterpolateCommand(element, lineNumber, color))
    case s"//${_}" => Comment()
    case s"" => EmptyLine()
    case _ => throw new IllegalArgumentException(s"Error: Couldn't parse command at line ${lineNumber}. '${command}'")
  }

  def CommandsToElements(commands: List[Command]): List[Element] = commands match {
    case BoundingBoxCommand(x1,y1,x2,y2) :: _ => RemoveCoordinatesOutsideBoundingArea(x1.toInt, y1.toInt, x2.toInt, y2.toInt, CommandsToElements(commands.tail))
    case LineCommand(x1, y1, x2, y2,color) :: _ => Line(Line(x1.toInt,y1.toInt,x2.toInt,y2.toInt,color)) :: CommandsToElements(commands.tail)
    case RectangleCommand(x1, y1, x2, y2,color) :: _ => Rectangle(Rect(x1.toInt,y1.toInt,x2.toInt,y2.toInt, color)) :: CommandsToElements(commands.tail)
    case CircleCommand(x1, y1, r,color) :: _ => Circle(DrawCircle(x1.toInt, y1.toInt, r.toInt, color)) :: CommandsToElements(commands.tail)
    case DrawCommand(color, elements, lineNumber) :: _ => InterpolateDrawCommand(color, elements, lineNumber) ::: CommandsToElements(commands.tail)
    case TextCommand(x1,y1,text,color) :: _ => Text(x1,y1,text,color) :: CommandsToElements(commands.tail)
    case FillCommand(strokeColor, fillColor, element) :: _ => Fill(strokeColor, fillColor, CommandsToElements(List(element)).head) :: CommandsToElements(commands.tail)
    case (Comment() | EmptyLine()) :: _ => CommandsToElements(commands.tail)
    case _ => List.empty
  }

  def RemoveCoordinatesOutsideBoundingArea(x1: Int, y1: Int, x2: Int, y2: Int, elementsInside: List[Element]): List[Element] = {
    elementsInside.map(s => {
      if (s.isInstanceOf[Shape]) {
        Circle(s.asInstanceOf[Shape].points.filter(p => {
          p.x <= x2 && p.x >= x1 && p.y <= y2 && p.y >= y1
        }))
      } else {
        s
      }
    })
  }

  def Fill(strokeColor: String, fillColor: String, element: Element ): Element = element match {
    case Rectangle(points) => Rectangle(ReactangleFill(fillColor, RectangleStartingPoint(points.head, points.tail, fillColor), points, HashSet() ++ points.map(p => (p.x, p.y))))
    case Circle(points) => Circle(CircleFill(fillColor, CircleCenter(points), points, Radius(points)))
    case _ => element
  }

  def CircleCenter(points: List[Point]): Point = {
    new Point(
      points.minBy(p => p.x).x + Radius(points),
      points.minBy(p => p.y).y + Radius(points),
      points.head.color)
  }

  def Radius(points: List[Point]): Int = {
    (points.maxBy(p => p.x).x - points.minBy(p => p.x).x)/2
  }

  def RectangleStartingPoint(point: Point, points: List[Point], color: String): Point = {
    if(points == List.empty) {
      return new Point(point.x+1, point.y+1, color)
    }
    if(point.y < points.head.y || point.x < points.head.x) {
      RectangleStartingPoint(point, points.tail, color)
    } else {
      RectangleStartingPoint(points.head, points.tail, color)
    }
  }

  def CircleFill(color: String, point: Point, points: List[Point], radius: Int): List[Point] = {
    ((((
      FillCircle(color, new Point(point.x+1, point.y+1, color), DrawCircle(point.x+1, point.y+1, radius-1, color), radius-2):::
      FillCircle(color, new Point(point.x-1, point.y-1, color), DrawCircle(point.x-1, point.y-1, radius-1, color), radius-2)):::
      FillCircle(color, new Point(point.x+1, point.y-1, color), DrawCircle(point.x+1, point.y-1, radius-1, color), radius-2)):::
      FillCircle(color, new Point(point.x-1, point.y+1, color), DrawCircle(point.x-1, point.y+1, radius-1, color), radius-2))):::
      points
  }

  def FillCircle(color: String, point: Point, points: List[Point], radius: Int): List[Point] = radius match {
    case 0 => new Point(point.x, point.y, color)::DrawCircle(point.x, point.y, 1, color)
    case _ =>  points ::: FillCircle(color, point, DrawCircle(point.x, point.y, radius, color), radius-1)
  }

  def ReactangleFill(color: String, point: Point, points: List[Point], visited: HashSet[(Int, Int)]): List[Point] = {
    if(visited(point.x, point.y)) return points
    ReactangleFill(color, nextPointUtil(point, visited), point :: points, visited + ((point.x, point.y)))
  }

  def nextPointUtil(point: Point, visited: HashSet[(Int, Int)]): Point = {
    if(!visited(point.x, point.y+1)) {
      //UP
      new Point(point.x, point.y +1, point.color)
    } else if(!visited(point.x, point.y-1)) {
      //DOWN
      new Point(point.x, point.y - 1, point.color)
    }else if(!visited(point.x+1, point.y)) {
      //RIGHT
      new Point(point.x + 1, point.y, point.color)
    } else {
      //LEFT
      new Point(point.x -1, point.y, point.color)
    }
  }

  def InterpolateDrawCommand(color: String, objects: String, lineNumber: Int): List[Element] = objects match {
    case s"($cmd ($i1) $i2) $rest" => CommandsToElements(List(InterpolateCommand(s"($cmd ($i1) $i2)", lineNumber, color))) ::: InterpolateDrawCommand(color, rest, lineNumber)
    case s"($cmd ($i1) $i2)" => CommandsToElements(List(InterpolateCommand(s"($cmd ($i1) $i2)", lineNumber, color)))
    case s"($cmd)) $rest" => CommandsToElements(List(InterpolateCommand(s"($cmd))", lineNumber, color))) ::: InterpolateDrawCommand(color, rest, lineNumber)
    case s"($cmd))" => CommandsToElements(List(InterpolateCommand(s"($cmd))", lineNumber, color)))
    case "" => List.empty
    case other => throw new IllegalArgumentException(s"Error: Couldn't parse command at line ${lineNumber}. '${other}'")
  }

  def Line(x1: Int, y1: Int, x2: Int, y2: Int, color: String, slopeError: Int = 0, first: Boolean = true): List[Point] = {
    if (first) {
      if (x1 == x2) {
        return VerticalLine(x1, y1, y2, color)
      }
    }

    if(x1 >= x2) {
      return List[Point](new Point(x2, y2, color))
    }

    if(slopeError + 2 * (y2 - y1) >= 0) {
      new Point(x1, y1, color) :: Line(x1 + 1, y1 + 1, x2, y2, color, slopeError - 2 * (x2 - x1) + 2 * (y2 - y1), false)
    } else {
      new Point(x1, y1, color) :: Line(x1 + 1, y1, x2, y2, color, slopeError + 2 * (y2 - y1), false)
    }

  }

  def VerticalLine(x: Int, y1: Int, y2: Int, color: String): List[Point] = (y1, y2) match {
    case i if i._1 == i._2 => List[Point](new Point(x, y1, color))
    case i if i._1 > i._2 => new Point(x, y2, color) :: VerticalLine(x, y1, y2 + 1, color)
    case i if i._1 < i._2 => new Point(x, y2, color) :: VerticalLine(x, y1, y2 - 1, color)
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

  def DrawCircle(x0: Int, y0: Int, r: Int, color: String): List[Point] = {
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
