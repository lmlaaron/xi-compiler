// method call multiple args single return
use io
foo(a:int, b:bool, c:int[]):int {
  println("in foo")
  println({a+48} + c)
  if b {println("b is true")return 3}
  else {println("b is false")return 4}
}
main(argv:int[][]) {
  println({foo(5, 3 < 4, "well")+48})
  println("after foo")
}
