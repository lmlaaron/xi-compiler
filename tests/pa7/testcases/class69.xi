// class attributes should be visible everywhere in the module
class Point {
    x,y: int
    size: int = 10

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        return this
    }
}

class AnotherPoint {
    x,y: int
	size: int = Point.size + 10

	initPoint(x0: int, y0: int): AnotherPoint {
        x = x0
        y = y0
        return this
    }
}

class HappyColoredPoint extends AnotherPoint {
    x,y: int
    size: int = AnotherPoint.size + 20

    initPoint(x0: int, y0: int): HappyColoredPoint {
        x = x0
        y = y0
        return this
    }
}

main(args: int[][]) {
    println(Point.size)
    println(AnotherPoint.size)
    println(HappyColoredPoint.size)
}
