use io
use conv

// global object declaration
class Point {
    x,y: int

    initPoint(x0: int, y0: int): Point {
        x = x0
        y = y0
        return this
    }
}

p1:Point

main(args: int[][]) {
    p1 = new Point.initPoint(2, 2)
    println(unparseInt(p1.x))
}
