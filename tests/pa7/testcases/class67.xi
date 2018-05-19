use io
use conv

// inheritence complications
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

class HappyColoredPoint extends ColoredPoint {
    x,y: int

    initPoint(x0: int, y0: int): HappyColoredPoint {
        x = x0
        y = y0
        return this
    }
}

main(args: int[][]) {
    p1:ColoredPoint
    p1 = new HappyColoredPoint.initPoint(2, 2)
    println(unparseInt(p1.x))
    println(unparseInt(p1.y))
}
