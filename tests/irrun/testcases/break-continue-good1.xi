use io
use conv

main(argv:int[][]) {
    x : int = 0
    while (x < 5) {
        x = x + 1
        break
    }
    println(unparseInt(x))
    y : int = 0
    while (x < 5) {
        x = x + 1
        continue
        y = y + 1
    }
    println(unparseInt(y))
}