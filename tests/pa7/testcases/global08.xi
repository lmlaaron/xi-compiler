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
    if (p1 == null) {
      println("p1 is null")
    }
    if (p1 != null) {
      println("p1 is not null")
    }
    p1 = new Point.initPoint(2, 2)
    if (p1 == null) {
      println("p1 is null")
    }
    if (p1 != null) {
      println("p1 is not null")
    }
}
