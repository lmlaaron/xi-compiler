use io
use conv

a: int[5]

main(args: int[][]) {
    for x in a {
      println(unparseInt(x))
    }
}