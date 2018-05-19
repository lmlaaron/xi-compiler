use io
use conv

// composition
class Point {
    x,y: int
    size: int

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        size = 5
        return this
    }
}

class AnotherPoint extends Point{
    x,y: int
	size: int
    cp: ColoredPoint

	initPoint(x0: int, y0: int): AnotherPoint {
        x = x0
        y = y0
        size = 10
        cp = ColoredPoint.initPoint(x0, y0)
        return this
    }
}

class ColoredPoint extends Point{
    x,y: int
    size: int
    ap: AnotherPoint

    initPoint(x0: int, y0: int): ColoredPoint {
      x = x0
      y = y0
      size = 20
      ap = AnotherPoint.initPoint(x0, y0)
      return this
    }
}

main(args: int[][]) {
    p: Point
    p = new Point.initPoint(0, 0)
    ap: AnotherPoint
    ap = new AnotherPoint.initPoint(1, 1)
    cp: ColoredPoint
    cp = new ColoredPoint.initPoint(2, 2)
    println(unparseInt(p.size))
    println(unparseInt(ap.size))
    println(unparseInt(cp.size))
}
