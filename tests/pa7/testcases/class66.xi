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
    p1:Point
    p1 = new HappyPoint.initPoint(2, 2)
    println(p1.x)
    println(p1.y)
}
