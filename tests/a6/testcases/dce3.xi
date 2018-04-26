use conv
use io
main(argv:int[][]) {
  x:int =0
  y:int = 30000000
  z:int
  while ( y > 0 ) {
    x = y * ( y -1 ) *(y-2)/(y+1)/(y+2)
    y = y - 1
  }
  z = 5
  println(unparseInt(z))
}

