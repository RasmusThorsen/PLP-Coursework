package Scala

object Painter {

  def Line(x1: Int, y1: Int, x2: Int, y2: Int, slopeError: Int = 0): List[(Int, Int)] = {
    if(x1 >= x2) {
      return List[(Int, Int)]((x2, y2))
    }

    if(slopeError + 2 * (y2 - y1) >= 0) {
      (x1, y1) :: Line(x1 + 1, y1 + 1, x2, y2, slopeError - 2 * (x2 - x1) + 2 * (y2 - y1))
    } else {
      (x1, y1) :: Line(x1 + 1, y1, x2, y2, slopeError + 2 * (y2 - y1))
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
