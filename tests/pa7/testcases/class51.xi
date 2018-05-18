// this
class Point {
    x,y: int

    initPoint(x: int, y: int): Point {
        this.x = x
        this.y = y
        return this
    }
}

main(args: int[][]) {
    p1:Point
    p1 = new Point.initPoint(2, 2)
    println(p1.x)
    println(p1.y)
}
