use io

// == and != between classes
class Point {
    x,y: int

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        return this
    }

    equals(p: Point): bool {
        return this == p
    }
}

class ColoredPoint extends Point{
    x,y: int

    initPoint(x0: int, y0: int): ColoredPoint {
        x = x0
        y = y0
        return this
    }

    equals(p: ColoredPoint): bool {
        return this == p
    }
}

class HappyColoredPoint extends ColoredPoint {
    x,y: int

    initPoint(x0: int, y0: int): HappyColoredPoint {
        x = x0
        y = y0
        return this
    }

    equals(p: HappyColoredPoint): bool {
        return this == p
    }
}

main(args: int[][]) {
    p1:Point
    p2:ColoredPoint
    p3:HappyColoredPoint
    p1 = new Point.initPoint(2, 2)
    p2 = new ColoredPoint.initPoint(3, 3)
    p3 = new HappyColoredPoint.initPoint(4, 4)
    if (!p1.equals(p1)) {
      println("p1 is not p1")
    }
    if (p1.equals(p1)) {
      println("p1 is p1")
    }
    if (!p1.equals(p2)) {
      println("p1 is not p2")
    }
    if (p1.equals(p2)) {
      println("p1 is p2")
    }
    if (!p1.equals(p3)) {
      println("p1 is not p3")
    }
    if (p1.equals(p3)) {
      println("p1 is p3")
    }
    if (!p2.equals(p3)) {
      println("p2 is not p3")
    }
    if (p2.equals(p3)) {
      println("p2 is p3")
    }
}
