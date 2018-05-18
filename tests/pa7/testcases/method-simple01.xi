use io
use conv

class X {
    f(a : int) : int {
        return a
    }
}

main (argv: int[][]) {
    println(unparseInt(new X.f(1)))
}