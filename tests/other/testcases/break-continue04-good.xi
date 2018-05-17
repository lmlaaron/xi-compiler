use io
use conv

// break out of one loop but not all loops
main(argv:int[][]) {
  x: int = 0
  while (x < 5) {
    y: int = 0
    while (y < 5) {
      if (y == 3) {
        break
      }
      y = y + 1
    }
    x = x + 1
    println(unparseInt(x))
    println(unparseInt(y))
  }
}