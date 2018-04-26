use io
use conv
foo():int {
   return 50
}
main(argv:int[][]) {
   println(unparseInt(foo()))
}
