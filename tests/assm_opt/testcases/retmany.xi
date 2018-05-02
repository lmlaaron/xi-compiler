use io
use conv

retmany() : int, int, int, int, int, int, int {
    return 1,2,3,4,5,6,7
}

main(argv:int[][]) {
    a : int, b : int, c : int, d : int, e : int, f : int, g : int = retmany()
    println(unparseInt(a))
}