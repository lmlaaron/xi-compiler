a:int[3][3]

main(args: int[][]) {
    x:int = 0
    y:int = 0
    while (x < 3) {
      while (y < 3) {
        println(a[x][y])
        y = y + 1
      }
      x = x + 1
    }
}