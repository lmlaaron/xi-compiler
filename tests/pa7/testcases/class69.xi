use io
use conv

// class attributes should be visible everywhere in the module
class Point {
    x,y: int
    size: int

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        size = 10
        return this
    }
}

class AnotherPoint {
    x,y: int
	size: int

	initPoint(x0: int, y0: int): AnotherPoint {
        x = x0
        y = y0
        size = new Point.initPoint(x0, y0).size + 10
        return this
    }
}

class HappyColoredPoint extends AnotherPoint {

    initPoint(x0: int, y0: int): HappyColoredPoint {
        x = x0
        y = y0
        size = new AnotherPoint.initPoint(x0, y0).size + 10
        return this
    }
}

main(args: int[][]) {
    println(unparseInt(new Point.initPoint(0, 0).size))
    println(unparseInt(new AnotherPoint.initPoint(0, 0).size))
    println(unparseInt(new HappyColoredPoint.initPoint(0, 0).size))
}
