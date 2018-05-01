// method call 1 arg single return
use io
foo(a:int):int {
  println("in foo")
  println({a+48})
  return 9
}
main(argv:int[][]) {
  println({48+foo(3)})
  println("after foo")
}
