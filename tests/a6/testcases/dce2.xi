use conv
use io

fact(a:int):int {
  i:int=0
  while (i < 10000 ) {
    i=i+1
  }
  return 1
}

main(argv:int[][]) {
  //x:int,_ =parseInt(argv[1])
  x:int =0
  y:int = 100000000
  z:int
  while ( y > 0 ) {
    while (x<10000) {
      x=x+1
      z=fact(100)
    }
    y = y - 1 
  }
  z=5
  println(unparseInt(z))
}

