use io use conv
main(argv:int[][]) {
  i:int = 0; x:int = 100000000; y:int = -100000000+2
  a:int = (x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
  b:int = 0
  while i < 3000000 {
    b = b+(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b-(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b+(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b-(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b+(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b-(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b+(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b-(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b+(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    b = b-(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)*(x+y)
    i = i + 1}
  println(unparseInt(b))
}