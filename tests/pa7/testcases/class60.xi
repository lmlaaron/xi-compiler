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

foo(): int {
	return Point.size
}

main(args: int[][]) {
    println(foo())
}
