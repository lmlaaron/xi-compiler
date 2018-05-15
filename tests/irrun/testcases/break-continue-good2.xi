use io
use conv

main(argv:int[][]) {
    x : int = 0
    while (x < 5) {
      y:int = 0
      while (y < 5) {
        y = y + 1
        break
      }
      println(unparseInt(x))
      println(unparseInt(y))
      x = x + 1
    }
}