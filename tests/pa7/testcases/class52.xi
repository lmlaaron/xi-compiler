// no name collisions of any kind
class Point {
    x,y: int
    size: int = 10

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        return this
    }
}

class ColoredPoint extends Point{
    x,y: int
    size: int = 20

    initPoint(x0: int, y0: int): ColoredPoint {
        x = x0
        y = y0
        return this
    }
}

main(args: int[][]) {
    p1:Point
    p2:ColoredPoint
    p1 = new Point.initPoint(2, 2)
    p2 = new ColoredPoint.initPoint(2, 2)
    println(p1.size)
    println(p2.size)
}
