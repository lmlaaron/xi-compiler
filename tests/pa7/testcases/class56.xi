// attributes of same name within parent & child classes
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
    p2:ColoredPoint
    p3:Point
    p1 = new Point.initPoint(2, 2)
    p2 = new ColoredPoint.initPoint(3, 3)
    if (p1 != null) {
      println("p1 is not null")
    } 
    if (p2 != null) {
      println("p2 is not null")
    }
    if (p3 != null) {
      println("p3 is not null")
    }
}
