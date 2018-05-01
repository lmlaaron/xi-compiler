// procedure 1 arg
use io
foo(a:int) {
  println("in foo")
  println({a+48})
}
main(argv:int[][]) {
  foo(5)
  println("after foo")
}
