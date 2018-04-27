use io use conv
main(argv:int[][]) {
  x:int =0
  i:int = 0
  z:int = 1
  y:int = x
  a:int 
  b:int
  c:int
  d:int
  while (i < 100000000 ) {
    y  = x
    if ( i % 2 == 0 ) { 
    a = y
    b=a
    c=b
    d=c
    } else {
    b=x
    a=b
    d=a
    }
    z =d

    i = i + 1
  }
  println(unparseInt(z))
}

