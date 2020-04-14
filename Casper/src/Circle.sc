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

val c1 = Circle(0, 0, 3)
// Outputs:
// List(
//    (0,3), (0,3), (0,-3), (0,-3),
//    (3,0), (-3,0), (3,0), (-3,0),
//    (1,3), (-1,3), (1,-3), (-1,-3),
//    (3,1), (-3,1), (3,-1), (-3,-1),
//    (2,2), (-2,2), (2,-2), (-2,-2)
// )
