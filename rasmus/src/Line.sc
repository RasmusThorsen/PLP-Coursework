def LineImperative(x1: Int, y1: Int, x2 : Int, y2: Int): Unit = {
  val m_new = 2 * (y2 - y1)
  var slope_error_new = m_new - (x2 - x1)

  var x = x1
  var y = y1
  while ( {x <= x2} ) {
    println("(" + x + "," + y + ")\n")
    // Add slope to increment angle formed
    slope_error_new += m_new
    // Slope error reached limit, time to
    // increment y and update slope error.
    if (slope_error_new >= 0) {
      y += 1
      slope_error_new -= 2 * (x2 - x1)
    }

    x += 1
  }
}

LineImperative(0, 0, 9, 4);

// A little bit off??? Close enough?
// Think it is because the slopeError is calculated with new coords, and not the starting coords.
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

Line(0, 0, 9, 4);
