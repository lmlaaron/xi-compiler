// override methods
class Point {
    x,y: int

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        return this
    }

    unknownCalculcation(): int {
        return x+y
    }
}

class ColoredPoint extends Point{
    x,y: int

    initPoint(x0: int, y0: int): ColoredPoint {
        x = x0
        y = y0
        return this
    }

    unknownCalculation(): int {
        return x-y
    }
}

main(args: int[][]) {
    p1:Point
    p2:ColoredPoint
    p1 = new Point.initPoint(2, 2)
    p2 = new ColoredPoint.initPoint(2, 2)
    println(p1.unknownCalculation())
    println(p2.unknownCalculation())
}
