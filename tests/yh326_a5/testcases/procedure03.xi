// procedure multiple args
use io
foo(a:int, b:bool, c:int[]) {
  println("in foo")
  println({a+48} + c)
  if b println("b is true")
  else println("b is false")
}
main(argv:int[][]) {
  foo(5, 3 < 4, "well")
  println("after foo")
}
