use io
use conv

global : int = 1

main(argv : int[][]) {
    println(unparseInt(global))
    global = global + 1
    println(unparseInt(global))
}