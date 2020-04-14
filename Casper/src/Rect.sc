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


def boundingBox(x1: Int, y1: Int, x2: Int, y2: Int) = {
  return Rect(x1)
}


// Small bug, start and end points are included twice
// Maybe return List.empty in base cases?
Rect(1, 1, 4, 4)

Rect(1,2,3,4)