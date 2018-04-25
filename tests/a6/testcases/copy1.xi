use io use conv
main(argv:int[][]) {
  x:int,_ =parseInt(argv[1])
  y:int = x
  z:int = 3+y
  println(unparseInt(z))
}

