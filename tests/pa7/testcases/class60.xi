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

foo(): int {
	return new Point.initPoint(1, 1).size
}

main(args: int[][]) {
    println(unparseInt(foo()))
}
