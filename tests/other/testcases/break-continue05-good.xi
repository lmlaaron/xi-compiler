use io
use conv

// continue out of one loop but not all loops
main(argv:int[][]) {
  x: int = 0
  while (x < 5) {
    y: int = 0
    while (y < 5) {
      if (y == 3) {
        y = y + 1
        continue
      }
      x = x + 1
      y = y + 1
      println(unparseInt(x))
      println(unparseInt(y))
    }
  }
}