use io
use conv

class X {
    i : int
}

main(argv : int[][]) {
    x : X = new X
    x.i = 1
    println(unparseInt(x.i))
}