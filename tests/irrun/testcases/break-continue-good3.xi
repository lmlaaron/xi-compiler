use io
use conv

main(argv:int[][]) {
    x : int = 0
    while (x < 5) {
      if (x == 5) {
        a:int = 1
        break
      }
      println(unparseInt(x))
      x = x + 1
    }
    println(unparseInt(x))
}