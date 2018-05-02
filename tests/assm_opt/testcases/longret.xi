// procedure multiple args
// to test the calling convention
use io
use conv

g3():int, int, int {return 1, 2, 3}

main(argv:int[][]) {
   g3r1:int,g3r2:int,a:int=g3()
   println(unparseInt(a))

}
