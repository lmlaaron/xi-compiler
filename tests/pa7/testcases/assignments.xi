use io
use conv

main(argv:int[][]) {
    a : int[5]
    b : int = 0
    for x in a {
        b = b + x
    }
    println(unparseInt(b))
}

