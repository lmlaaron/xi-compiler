use io
use conv

a:int[3][3][3]

main(args: int[][]) {
    x:int = 0
    y:int = 0
    z:int = 0
    while (x < 3) {
      while (y < 3) {
        while (z < 3) {
          println(unparseInt(a[x][y][z]))
          z = z + 1
        }
        y = y + 1
      }
      x = x + 1
    }
}