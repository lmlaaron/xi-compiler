use io

main(argv:int[][]) {
    x : int = 0
    while (x < 5) {
      y:int = 0
      while (y < 5) {
        y = y + 1
        break
      }
      x = x + 1
    }
}