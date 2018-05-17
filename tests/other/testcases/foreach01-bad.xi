use io
use conv

main(argv:int[][]) {
    a : int[5]
    a[0] = 0
    a[1] = 1
    a[2] = 2
    a[3] = 3
    a[4] = 4
    for x in a {
          if (x == 2) {
                  break
                }
          println(unparseInt(x))
        }
}