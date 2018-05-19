use io
use conv

// composition
class Point {
    x,y: int
    size: int
    ap: AnotherPoint

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        size = 10
        ap = new AnotherPoint.initPoint(x0, y0)
        ap.size = ap.size + 10
        return this
    }
}

class AnotherPoint {
    x,y: int
	size: int
    p: Point

	initPoint(x0: int, y0: int): AnotherPoint {
        x = x0
        y = y0
        size = new Point.initPoint(x0, y0).size + 10
        p = new Point.initPoint(x0, y0)
        p.size = p.size + 30
        return this
    }
}

main(args: int[][]) {
    p: Point
    p = new Point.initPoint(0, 0)
    ap: AnotherPoint
    ap = new AnotherPoint.initPoint(1, 1)
    println(unparseInt(p.size))
    println(unparseInt(p.ap.size))
    println(unparseInt(ap.size))
    println(unparseInt(ap.p.size))
}
