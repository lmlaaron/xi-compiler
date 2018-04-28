use io
use conv

foo_many(a: int, b: int, c: int, d: int, e: int, f: int, g: int, h: int ) : int {
    println(unparseInt(a))
    println(unparseInt(b))
    println(unparseInt(c))
    println(unparseInt(d))
    println(unparseInt(e))
    println(unparseInt(f))
    println(unparseInt(g))
    println(unparseInt(h))
    return a + b + c + d + e + f + g + h
}

main(args: int[][]) {
  println(unparseInt(foo_many(1, 2, 3, 4, 5, 6, 7, 8)))
}
