use io
use conv

a:int[3][3]

main(args: int[][]) {
    x:int = 0
    y:int = 0
    while (x < 3) {
      y = 0
      while (y < 3) {
        println(unparseInt(a[x][y]))
        y = y + 1
      }
      x = x + 1
    }
}
