// == and != between classes
class Point {
    x,y: int

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        return this
    }
}

class ColoredPoint extends Point{
    x,y: int

    initPoint(x0: int, y0: int): ColoredPoint {
        x = x0
        y = y0
        return this
    }
}

main(args: int[][]) {
    p1:Point
    p2:Point
    p1 = new Point.initPoint(2, 2)
    p2 = new Point.initPoint(2, 2)
    if (p1 != p1) {
      println("p1 is not p1")
    }
    if (p1 == p1) {
      println("p1 is p1")
    }
    if (p1 != p2) {
      println("p1 is not p2")
    }
    if (p1 == p2) {
      println("p1 is p2")
    }
}
