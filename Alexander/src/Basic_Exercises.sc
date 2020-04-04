sealed abstract class PointList
case class Nil() extends PointList
case class Cons(hd: (Int, Int), tl: (PointList,PointList) ) extends PointList


def line(x0: Int, y0: Int, x1: Int, y1: Int): PointList = {
  lineAlgorithm(x0,y0,x1,y1);
}

def lineAlgorithm(x0: Int, y0: Int, x1: Int, y1: Int, pointList: PointList): PointList = (x0,x1) match {
  case x0 == x1 => pointList
}

def lineDrawingEnd(x0: Int, x1: Int): Boolean = {
  case x0 < x1 => false
  case x0 >= x1 => true
}

def differenceCalculation(deltaY: Int, deltaX: Int): Int = {
  2*deltaY - deltaX
}

def delta(coordinate0: Int, coordinate1: Int): Int = {
  coordinate1 - coordinate0
}

