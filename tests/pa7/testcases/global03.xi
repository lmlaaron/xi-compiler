use io
use conv

a: int[5]

main(args: int[][]) {
    a[2]=5
    for x in a {
      println(unparseInt(x))
    }
}
