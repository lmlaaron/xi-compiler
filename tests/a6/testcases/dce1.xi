use conv
use io

main(argv:int[][]) {
  //x:int,_ =parseInt(argv[1])
  x:int =0
  i:int = 0
  z:int = 1
  y:int = x
  while (i < 50000000 ) {
    i = i + 1
    y = i*i/i*i/i*i
    y = 15
    y = 5
    z = 10
  }
  println(unparseInt(y))
}

