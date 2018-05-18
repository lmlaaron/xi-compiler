use io
use conv

main(argv:int[][]) {
    x : int[5]
    x[3] = 3
    println(unparseInt(x[0]))
    println(unparseInt(x[1]))
    println(unparseInt(x[2]))
    println(unparseInt(x[3]))
    println(unparseInt(x[4]))
}